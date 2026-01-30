package compilamos.manana.partygame.game.engine;


import compilamos.manana.partygame.api.exception.ApiException;
import compilamos.manana.partygame.api.exception.ErrorCode;
import compilamos.manana.partygame.config.GameConfig;
import compilamos.manana.partygame.game.command.*;
import compilamos.manana.partygame.game.event.DomainEvent;
import compilamos.manana.partygame.game.event.EventBuilder;
import compilamos.manana.partygame.game.model.*;
import compilamos.manana.partygame.game.model.question.Question;
import compilamos.manana.partygame.game.model.question.RoundQuestions;
import compilamos.manana.partygame.game.model.snapshot.PlayerSnapshot;
import compilamos.manana.partygame.game.model.snapshot.HostSnapshot;
import compilamos.manana.partygame.game.question.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class GameEngine {
    private final GameContext context;
    private final GameConfig gameConfig;
    private final QuestionService questionService;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);


    public GameEngine(String roomCode, String gameId, GameConfig gameConfig, QuestionService questionService) {
        this.gameConfig = gameConfig;
        this.questionService = questionService;
        this.context = GameContext.builder()
                .roomCode(roomCode)
                .gameId(gameId)
                .gameState(GameState.LOBBY)
                .roundNumber(0)
                .cycleNumber(0)
                .build();
    }

    public GameContext getContext() {
        lock.readLock().lock();
        try {
            return context;
        } finally {
            lock.readLock().unlock();
        }
    }

    public HostSnapshot getHostSnapshot() {
        lock.readLock().lock();
        try {
            var impostor = context.getPlayers()
                    .values()
                    .stream()
                    .filter(java.util.Objects::nonNull)
                    .filter(Player::isImpostor)
                    .findFirst()
                    .orElse(Player.builder().
                            playerId("N/A").
                            name("N/A").
                            avatarId(0).
                            isImpostor(false).
                            state(PlayerState.NOT_IN_ROOM).
                            connectionState(ConnectionState.DISCONNECTED).
                            currentQuestion(null).
                            build());

            return new HostSnapshot(
                    context.getRoomCode(),
                    context.getGameId(),
                    context.getGameState(),
                    context.getHostConnectionState(),
                    0,
                    0,
                    context.getPlayers().values().stream().map(
                            p -> new PlayerSnapshot(p.getPlayerId(),
                                    p.getName(),
                                    p.getAvatarId(),
                                    p.isImpostor(),
                                    p.getState(),
                                    p.getConnectionState(),
                                    p.getCurrentQuestion()
                            )).toList(),
                    new PlayerSnapshot(
                            impostor.getPlayerId(),
                            impostor.getName(),
                            impostor.getAvatarId(),
                            impostor.isImpostor(),
                            impostor.getState(),
                            impostor.getConnectionState(),
                            impostor.getCurrentQuestion()
                    ),
                    context.getPlayersQuestion(),
                    context.getImpostorQuestion()
            );
        } finally {
            lock.readLock().unlock();
        }
    }

    public DomainEvent getPlayerSnapshot(String playerId) {
        lock.readLock().lock();
        try {
            Player player = context.getPlayers().get(playerId);
            if (player == null) {
                throw new ApiException(
                        ErrorCode.NOT_FOUND,
                        "Player not found: " + playerId,
                        HttpStatus.NOT_FOUND
                );
            }

            return EventBuilder.playerSnapshot(context, new PlayerSnapshot(
                    player.getPlayerId(),
                    player.getName(),
                    player.getAvatarId(),
                    player.isImpostor(),
                    player.getState(),
                    player.getConnectionState(),
                    player.getCurrentQuestion()
            ));

        } finally {
            lock.readLock().unlock();
        }
    }

    public List<DomainEvent> handle(Command command) {
        lock.writeLock().lock();
        try {
            return switch (command) {
                case JoinRoomCommand join -> handleJoinRoom(join);
                case LeaveRoomCommand leave -> handleLeaveRoom(leave);
                case PlayerDisconnectCommand disconnect -> handlePlayerDisconnect(disconnect);
                case PlayerConnectCommand connect -> handlePlayerConnect(connect.playerId());
                case HostDisconnectCommand disconnect -> handleHostDisconnect(disconnect);
                case HostConnectCommand connect -> handleHostConnect(connect);
                case StartGameCommand startGameCommand -> handleStartGame(startGameCommand);
                case CustomEventCommand customEventCommand -> handleCustomEvent(customEventCommand);
                case NextRoundCommand nextRoundCommand -> handleNextRound(nextRoundCommand);
                case SendAnswerCommand sendAnswerCommand -> handleSendAnswer(sendAnswerCommand);
                default -> throw new ApiException(ErrorCode.UNKNOWN_COMMAND,
                        "Unknown command: " + command.getClass().getSimpleName());
            };
        } finally {
            lock.writeLock().unlock();
        }
    }

    private List<DomainEvent> handleSendAnswer(SendAnswerCommand sendAnswerCommand) {
        log.info("Handling SendAnswer for playerId: {}", sendAnswerCommand.playerId());

        ensureState(GameState.RESPONDIENDO);
        ensureHostConnected();

        String playerId = sendAnswerCommand.playerId();
        Player player = context.getPlayers().get(playerId);
        if (player == null) {
            throw new ApiException(
                    ErrorCode.NOT_FOUND,
                    "Player not found: " + playerId,
                    HttpStatus.NOT_FOUND
            );
        }

        if (player.getState() != PlayerState.RESPONDIENDO) {
            throw new ApiException(
                    ErrorCode.INVALID_STATE,
                    "Player is not in RESPONDIENDO state: " + player.getState(),
                    HttpStatus.BAD_REQUEST
            );
        }

        // check if player has already answered
        Answer existingAnswer = context.getCurrentRoundAnswers().stream()
                .filter(a -> a.getPlayerId().equals(playerId))
                .findFirst()
                .orElse(null);

        // if already answered, replace

        if (existingAnswer != null) {
            context.getCurrentRoundAnswers().remove(existingAnswer);
        }

        // record answer
        Answer answer = new Answer(playerId, player.getName(), sendAnswerCommand.answerText());
        context.getCurrentRoundAnswers().add(answer);

        // update player state
        player.setState(PlayerState.RESPUESTA_ENVIADA);
        context.getPlayers().put(playerId, player);

        context.incrementCycleNumber();

        DomainEvent event = EventBuilder.respuestaEnviada(context, player, answer);

        return List.of(event);
    }

    private List<DomainEvent> handleNextRound(NextRoundCommand nextRoundCommand) {
        log.info("Handling NextRound for roomCode: {}", nextRoundCommand.roomCode());

        ensureState(GameState.EMPATE);
        ensureHostConnected();

        // check if max rounds reached
        if (context.getRoundNumber() >= gameConfig.getRoom().getMaxRounds()) {
            log.info("Round number exceeded for roomCode: {}", nextRoundCommand.roomCode());
            log.error("Max rounds reached: {} >= {}", context.getRoundNumber(), gameConfig.getRoom().getMaxRounds());
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Max rounds reached: " + context.getRoundNumber() + " >= " + gameConfig.getRoom().getMaxRounds(),
                    HttpStatus.BAD_REQUEST
            );
        }

        context.incrementRoundNumber();
        context.incrementCycleNumber();

        assignQuestionsToPlayers();

        var currentRoundsAnswers = context.getCurrentRoundAnswers();
        if (currentRoundsAnswers == null) currentRoundsAnswers = new ArrayList<>();

        context.getRoundsAnswersHistory().add(new ArrayList<>(currentRoundsAnswers));
        currentRoundsAnswers.clear();


        List<DomainEvent> events = new ArrayList<>();
        DomainEvent rondaIniciada = EventBuilder.nuevaRondaIniciada(context);
        events.add(rondaIniciada);

        context.getPlayers().values().forEach(p -> {
            p.setState(PlayerState.RESPONDIENDO);
            context.getPlayers().put(p.getPlayerId(), p);
            DomainEvent playerSnapshot = EventBuilder.preguntaAsignada(context, p);
            events.add(playerSnapshot);
        });

        return events;
    }

    private List<DomainEvent> handleStartGame(StartGameCommand start) {
        log.info("Handling StartGame for roomCode: {}", start.roomCode());

        var conjunto = start.conjunto() != null && !start.conjunto().isEmpty() ? start.conjunto() : context.getQuestionSetName();

        if (conjunto == null || conjunto.isEmpty()) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Question set name cannot be null or empty",
                    HttpStatus.BAD_REQUEST
            );
        }

        var maxRounds = start.maxRounds() > 0 ? start.maxRounds() : gameConfig.getRoom().getMaxRounds();

        ensureState(GameState.LOBBY, GameState.GANA_IMPOSTOR, GameState.GANAN_JUGADORES);
        ensureHostConnected();

        // player count >= minPlayers
        List<Player> players = context.getPlayers().values().stream().toList();
        if (players.size() < gameConfig.getRoom().getMinPlayers()) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Not enough players to start the game. Minimum required: " + gameConfig.getRoom().getMinPlayers(),
                    HttpStatus.BAD_REQUEST
            );
        }

        context.setQuestionSetName(conjunto);

        players.forEach(p -> {
            p.setState(PlayerState.ASIGNANDO_ROL);
            p.setImpostor(false);
            p.setCurrentQuestion(null);
            context.getPlayers().put(p.getPlayerId(), p);
        });

        // chose impostor
        int impostorIndex = (int) (Math.random() * players.size());
        Player impostor = players.get(impostorIndex);
        impostor.setImpostor(true);

        context.getPlayers().put(impostor.getPlayerId(), impostor);

        // - Cambiar estado del juego a IN_GAME.
        context.setGameState(GameState.ASIGNANDO_ROLES);

        // - Inicializar roundNumber a 1.
        context.setRoundNumber(1);
        context.setCycleNumber(1);

        // - Limpiar preguntas asignadas
        context.getRoundsQuestionsHistory().clear();
        context.getCurrentRoundAnswers().clear();
        context.getRoundsAnswersHistory().clear();
        context.setPlayersQuestion(null);
        context.setImpostorQuestion(null);

        // - Asignar maxRounds.
        gameConfig.getRoom().setMaxRounds(start.maxRounds());

        List<DomainEvent> events = new ArrayList<>();

        events.add(EventBuilder.partidaIniciada(context));
        players.forEach(p -> {
            events.add(EventBuilder.rolesAsignados(context, p));
        });
        return events;

    }

    /**
     * Handler de HostDisconnect.
     * Reglas funcionales:
     * - Cambiar estado del host a DISCONNECTED.
     */
    private List<DomainEvent> handleHostDisconnect(HostDisconnectCommand disconnect) {
        log.info("Handling HostDisconnect for roomCode: {}", disconnect.roomCode());

        context.setHostConnectionState(ConnectionState.DISCONNECTED);

        context.incrementCycleNumber();

        DomainEvent event = EventBuilder.hostDisconnected(context);

        return List.of(event);
    }

    /**
     * Handler de HostDisconnect.
     * Reglas funcionales:
     * - Cambiar estado del host a DISCONNECTED.
     */
    private List<DomainEvent> handleHostConnect(HostConnectCommand connect) {
        log.info("Handling HostConnect for roomCode: {}", connect.roomCode());

        context.setHostConnectionState(ConnectionState.CONNECTED);

        context.incrementCycleNumber();

        DomainEvent event = EventBuilder.hostConnected(context);

        return List.of(event);
    }

    /**
     * Handler de PlayerDisconnect.
     * Reglas funcionales:
     * - Cambiar estado del jugador a DISCONNECTED.
     */
    private List<DomainEvent> handlePlayerDisconnect(PlayerDisconnectCommand disconnect) {
        log.info("Handling PlayerDisconnect for playerId: {}", disconnect.playerId());

        String playerId = disconnect.playerId();
        Player player = context.getPlayers().get(playerId);

        if (player == null) {
            throw new ApiException(
                    ErrorCode.NOT_FOUND,
                    "Player not found: " + playerId,
                    HttpStatus.NOT_FOUND
            );
        }

        log.info("Player found: {} with state {}", player.getName(), player.getState());

        Player updatedPlayer = Player.builder()
                .playerId(player.getPlayerId())
                .name(player.getName())
                .avatarId(player.getAvatarId())
                .isImpostor(player.isImpostor())
                .connectionState(ConnectionState.DISCONNECTED)
                .state(player.getState())
                .currentQuestion(player.getCurrentQuestion())
                .build();

        context.getPlayers().put(playerId, updatedPlayer);

        DomainEvent event = EventBuilder.playerDisconnected(context, updatedPlayer);

        context.incrementCycleNumber();

        return List.of(event);
    }

    public List<DomainEvent> handlePlayerConnect(String playerId) {
        log.info("Handling PlayerConnect for playerId: {}", playerId);

        Player player = context.getPlayers().get(playerId);

        log.info("Player before connect: {}", player);

        if (player == null) {
            throw new ApiException(
                    ErrorCode.NOT_FOUND,
                    "Player not found: " + playerId,
                    HttpStatus.NOT_FOUND
            );
        }

        Player updatedPlayer = Player.builder()
                .playerId(player.getPlayerId())
                .name(player.getName())
                .avatarId(player.getAvatarId())
                .state(PlayerState.IN_LOBBY)
                .connectionState(ConnectionState.CONNECTED)
                .build();

        context.getPlayers().put(playerId, updatedPlayer);

        context.incrementCycleNumber();

        return List.of(EventBuilder.playerConnected(context, updatedPlayer));
    }

    /**
     * Handler de JoinRoom.
     * Reglas funcionales:
     * - Solo se permite en LOBBY.
     * - No permitir playerId ni name duplicado.
     * - El jugador entra como IN_LOBBY y alcohol=0 (idealmente ya viene así, pero lo forzamos).
     */
    private List<DomainEvent> handleJoinRoom(JoinRoomCommand command) {
        ensureState(GameState.LOBBY);

        int currentPlayers = context.getPlayers().size();
        if (currentPlayers >= gameConfig.getRoom().getMaxPlayers()) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Room is full. Max players: " + gameConfig.getRoom().getMaxPlayers(),
                    HttpStatus.BAD_REQUEST
            );
        }

        Player newPlayer = command.player();
        if (newPlayer == null) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Player cannot be null",
                    HttpStatus.BAD_REQUEST
            );
        }

        String newPlayerId = newPlayer.getPlayerId();
        if (newPlayerId == null) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Player ID cannot be null",
                    HttpStatus.BAD_REQUEST
            );
        }

        String newPlayerName = newPlayer.getName();
        if (newPlayerName == null || newPlayerName.trim().isEmpty()) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Player name cannot be null",
                    HttpStatus.BAD_REQUEST
            );
        }

        int avatarId = newPlayer.getAvatarId();
        if (!gameConfig.isValidAvatarId(avatarId)) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Invalid avatar ID: " + avatarId,
                    HttpStatus.BAD_REQUEST
            );
        }

        // check if name or id already exists
        for (Player existingPlayer : context.getPlayers().values()) {
            if (existingPlayer.getPlayerId().equals(newPlayerId)) {
                throw new ApiException(
                        ErrorCode.VALIDATION_ERROR,
                        "Player ID already exists: " + newPlayerId,
                        HttpStatus.BAD_REQUEST
                );
            }
            if (existingPlayer.getName().equalsIgnoreCase(newPlayerName)) {
                throw new ApiException(
                        ErrorCode.VALIDATION_ERROR,
                        "Player name already exists: " + newPlayerName,
                        HttpStatus.BAD_REQUEST
                );
            }
        }

        // normalize new player state
        Player normalized = Player.builder()
                .playerId(newPlayerId)
                .name(newPlayerName)
                .avatarId(avatarId)
                .state(PlayerState.IN_LOBBY)
                .isImpostor(false)
                .build();

        context.getPlayers().put(newPlayerId, normalized);

        DomainEvent event = EventBuilder.playerJoined(context, normalized);

        context.incrementCycleNumber();

        return List.of(event);
    }

    /**
     * Handler de LeaveRoom.
     * @param leave LeaveRoomCommand
     * @return Lista de eventos generados
     */
    private List<DomainEvent> handleLeaveRoom(LeaveRoomCommand leave) {
        String playerId = leave.playerId();
        Player departingPlayer = context.getPlayers().get(playerId);
        if (departingPlayer == null) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Player not found: " + playerId,
                    HttpStatus.BAD_REQUEST
            );
        }

        context.getPlayers().remove(playerId);

        DomainEvent event = EventBuilder.playerLeft(context, departingPlayer);

        context.incrementCycleNumber();

        return List.of(event);
    }

    private List<DomainEvent> handleCustomEvent(CustomEventCommand customEvent) {
        log.info("Handling CustomEvent for roomCode: {}", customEvent.roomCode());

        DomainEvent event = EventBuilder.customEvent(context, customEvent.payload());

        return List.of(event);
    }


    private void assignQuestionsToPlayers() {
        List<String> excludedQuestions = context.getRoundsQuestionsHistory().stream()
                .flatMap(rq -> rq.getPreguntas().stream())
                .map(q -> String.valueOf(q.getId()))
                .toList();
        RoundQuestions roundQuestions = questionService.getRandomQuestion(excludedQuestions);

        // pick a random question for players and impostor
        List<Question> questions = new ArrayList<>(roundQuestions.getPreguntas());
        int playersQuestionIndex = (int) (Math.random() * questions.size());
        Question playersQuestion = questions.get(playersQuestionIndex);
        questions.remove(playersQuestionIndex);
        int impostorQuestionIndex = (int) (Math.random() * questions.size());
        Question impostorQuestion = questions.get(impostorQuestionIndex);
        context.setPlayersQuestion(playersQuestion);
        context.setImpostorQuestion(impostorQuestion);

        context.getPlayers().values().forEach(p -> {
           if (p.isImpostor()) {
               p.setCurrentQuestion(impostorQuestion);
           } else {
               p.setCurrentQuestion(playersQuestion);
           }
        });

        context.getRoundsQuestionsHistory().add(roundQuestions);
    }

    private void ensureState(GameState... expectedStates) {
        for (GameState expected : expectedStates) {
            if (context.getGameState() == expected) {
                return;
            }
        }
        throw new ApiException(
                ErrorCode.INVALID_STATE,
                "Invalid state. Expected one of " + List.of(expectedStates) + " but was " + context.getGameState()
        );
    }

    private void ensureState(GameState expected) {
        if (context.getGameState() != expected) {
            throw new ApiException(
                    ErrorCode.INVALID_STATE,
                    "Invalid state. Expected " + expected + " but was " + context.getGameState()
            );
        }
    }

    private void ensureHostConnected() {
        if (context.getHostConnectionState() != ConnectionState.CONNECTED) {
            throw new ApiException(
                    ErrorCode.HOST_DISCONNECTED,
                    "Host is disconnected",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

}
