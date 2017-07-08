package net.synchthia.systera;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.synchthia.api.systera.SysteraGrpc;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.punishment.PunishManager;
import net.synchthia.systera.util.DateUtil;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static net.synchthia.api.systera.SysteraProtos.*;
import static net.synchthia.api.systera.SysteraProtos.StreamType.*;

/**
 * @author misterT2525, Laica-Lunasys
 */
public class APIClient {
    private final ManagedChannel channel;
    private final SysteraGrpc.SysteraStub stub;
    private final SysteraGrpc.SysteraBlockingStub blockingStub;

    public APIClient(@NonNull String target) {
        channel = NettyChannelBuilder.forTarget(target).usePlaintext(true).nameResolverFactory(new DnsNameResolverProvider()).build();
        stub = SysteraGrpc.newStub(channel);
        blockingStub = SysteraGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private String toString(@NonNull UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    public void quitStream(@NonNull String name) {
        QuitStreamRequest request = QuitStreamRequest.newBuilder()
                .setName(name)
                .build();

        blockingStub.quitStream(request);
    }

    public void ping() {
        Empty request = Empty.newBuilder().build();

        stub.ping(request, new StreamObserver<Empty>() {
            @Override
            public void onNext(Empty value) {
                SysteraPlugin.getInstance().getLogger().log(Level.INFO, "### Welcome Back!");
                SysteraPlugin.getInstance().registerStream();
            }

            @SneakyThrows
            @Override
            public void onError(Throwable t) {
                Status status = Status.fromThrowable(t);

                if (status.getCode().toStatus() == Status.UNAVAILABLE) {
                    SysteraPlugin.getInstance().getLogger().log(Level.SEVERE, "StatusRuntimeException Occurred! Retrying...");
                    Thread.sleep(3000L);
                    ping();
                    return;
                }

                if (status.getCause() instanceof IOException) {
                    SysteraPlugin.getInstance().getLogger().log(Level.SEVERE, "IOException Occurred! API Server down?");
                    ping();
                    return;
                }

                System.out.println("Trace ===");
                System.out.println(status.getCause());
                System.out.println(status.getCode());
                System.out.println(status.getDescription());
                t.printStackTrace();
                System.out.println("Trace ===");
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void actionStream(@NonNull String name) {
        StreamRequest request = StreamRequest.newBuilder()
                .setName(name)
                .setType(CONNECT)
                .build();

        stub.actionStream(request, new StreamObserver<SysteraProtos.ActionStreamResponse>() {
            @Override
            public void onNext(ActionStreamResponse value) {
                switch (value.getType()) {
                    case RESTORED:
                        SysteraPlugin.getInstance().getLogger().log(Level.INFO, "### Connected!");
                        break;
                    case DISPATCH:
                        SysteraPlugin.getInstance().getLogger().log(Level.INFO, "Incoming Command: " + value.getCmd());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value.getCmd());
                        break;
                }
            }

            @SneakyThrows
            @Override
            public void onError(Throwable t) {
                ping();
            }

            @Override
            public void onCompleted() {
                SysteraPlugin.getInstance().getLogger().log(Level.INFO, "ActionStream connection closed!");
            }
        });
    }

    public void punishStream(@NonNull String name) {
        StreamRequest request = StreamRequest.newBuilder()
                .setName(name)
                .build();

        stub.punishStream(request, new StreamObserver<SysteraProtos.PunishStreamResponse>() {
            @Override
            public void onNext(PunishStreamResponse value) {
                if (value.getType().equals(StreamType.DISPATCH)) {
                    PunishEntry entry = value.getEntry();
                    String[] messages = PunishManager.punishMessage(entry);
                    PunishManager punishManager = new PunishManager(SysteraPlugin.getInstance());
                    punishManager.action(Bukkit.getPlayer(entry.getPunishedTo().getName()), entry.getLevel(), messages);
                    punishManager.broadcast(entry.getLevel().toString(), entry.getPunishedTo().getName(), entry.getReason());
                }
            }

            @SneakyThrows
            @Override
            public void onError(Throwable t) {
            }

            @Override
            public void onCompleted() {
                SysteraPlugin.getInstance().getLogger().log(Level.INFO, "PunishStream connection closed!");
            }
        });
    }

    public CompletableFuture<SysteraProtos.Empty> announce(@NonNull String target, @NonNull String message) {
        AnnounceRequest request = AnnounceRequest.newBuilder()
                .setTarget(target)
                .setMessage(message)
                .build();

        CompletableFuture<SysteraProtos.Empty> future = new CompletableFuture<>();
        stub.announce(request, new CompletableFutureObserver<>(future));

        return future;
    }

    public CompletableFuture<Boolean> initPlayerProfile(@NonNull UUID uuid, @NonNull String name, @NonNull String ipAddress) {
        InitPlayerProfileRequest request = InitPlayerProfileRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .setPlayerName(name)
                .setPlayerIPAddress(ipAddress)
                .build();

        CompletableFuture<SysteraProtos.InitPlayerProfileResponse> future = new CompletableFuture<>();
        stub.initPlayerProfile(request, new CompletableFutureObserver<>(future));

        return future.thenApply(InitPlayerProfileResponse::getHasProfile);
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfile(@NonNull UUID uuid) {
        FetchPlayerProfileRequest request = FetchPlayerProfileRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .build();

        CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> future = new CompletableFuture<>();
        stub.fetchPlayerProfile(request, new CompletableFutureObserver<>(future));

        return future;
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfileByName(@NonNull String name) {
        FetchPlayerProfileByNameRequest request = FetchPlayerProfileByNameRequest.newBuilder()
                .setPlayerName(name)
                .build();

        CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> future = new CompletableFuture<>();
        stub.fetchPlayerProfileByName(request, new CompletableFutureObserver<>(future));

        return future;
    }

    public CompletableFuture<SysteraProtos.Empty> setPlayerServer(@NonNull UUID uuid, String serverName) {
        SetPlayerServerRequest request = SetPlayerServerRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .setServerName(serverName)
                .build();

        CompletableFuture<SysteraProtos.Empty> future = new CompletableFuture<>();
        stub.setPlayerServer(request, new CompletableFutureObserver<>(future));

        return future;
    }

    public CompletableFuture<SysteraProtos.Empty> quitServer(@NonNull UUID uuid, String serverName) {
        RemovePlayerServerRequest request = RemovePlayerServerRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .setServerName(serverName)
                .build();

        CompletableFuture<SysteraProtos.Empty> future = new CompletableFuture<>();
        stub.removePlayerServer(request, new CompletableFutureObserver<>(future));

        return future;
    }

    public CompletableFuture<SysteraProtos.Empty> setPlayerSettings(@NonNull UUID uuid, String key, Boolean value) {
        SetPlayerSettingsRequest request = SetPlayerSettingsRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .setKey(key)
                .setValue(value)
                .build();

        CompletableFuture<SysteraProtos.Empty> future = new CompletableFuture<>();
        stub.setPlayerSettings(request, new CompletableFutureObserver<>(future));

        return future;
    }

    public CompletableFuture<SysteraProtos.GetPlayerPunishResponse> getPlayerPunishment(@NonNull UUID uuid, PunishLevel filterLevel, Boolean includeExpired) {
        GetPlayerPunishRequest request = GetPlayerPunishRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .setFilterLevel(filterLevel)
                .setIncludeExpired(includeExpired)
                .build();

        CompletableFuture<SysteraProtos.GetPlayerPunishResponse> future = new CompletableFuture<>();
        stub.getPlayerPunish(request, new CompletableFutureObserver<>(future));

        return future;
    }

    public CompletableFuture<SysteraProtos.SetPlayerPunishResponse> setPlayerPunishment(@NonNull Boolean remote, @NonNull Boolean force, PlayerData fromPlayer, PlayerData toPlayer, PunishLevel level, String reason, Long expire) {
        PunishEntry entry = PunishEntry.newBuilder()
                .setLevel(level)
                .setReason(reason)
                .setDate(DateUtil.getEpochMilliTime())
                .setExpire(expire)
                .setPunishedFrom(fromPlayer)
                .setPunishedTo(toPlayer)
                .build();

        SetPlayerPunishRequest request = SetPlayerPunishRequest.newBuilder()
                .setRemote(remote)
                .setForce(force)
                .setEntry(entry)
                .build();

        CompletableFuture<SysteraProtos.SetPlayerPunishResponse> future = new CompletableFuture<>();
        stub.setPlayerPunish(request, new CompletableFutureObserver<>(future));
        return future;
    }

    public CompletableFuture<SysteraProtos.FetchGroupsResponse> fetchGroups(@NonNull String serverName) {
        SysteraProtos.FetchGroupsRequest request = SysteraProtos.FetchGroupsRequest.newBuilder()
                .setServerName(serverName)
                .build();
        CompletableFuture<SysteraProtos.FetchGroupsResponse> future = new CompletableFuture<>();
        stub.fetchGroups(request, new CompletableFutureObserver<>(future));
        return future;
    }

    @RequiredArgsConstructor
    private static class CompletableFutureObserver<V> implements StreamObserver<V> {
        private final CompletableFuture<V> future;

        @Override
        public void onNext(V v) {
            future.complete(v);
        }

        @Override
        public void onError(Throwable throwable) {
            future.completeExceptionally(throwable);
        }

        @Override
        public void onCompleted() {
        }
    }
}