package net.synchthia.systera;

import com.google.protobuf.util.JsonFormat;
import io.grpc.ManagedChannel;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.synchthia.api.systera.SysteraGrpc;
import net.synchthia.api.systera.SysteraProtos;
import net.synchthia.systera.util.DateUtil;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static net.synchthia.api.systera.SysteraProtos.*;

/**
 * @author misterT2525, Laica-Lunasys
 */
public class APIClient {
    private final ManagedChannel channel;
    private final SysteraGrpc.SysteraStub stub;
    private final SysteraGrpc.SysteraBlockingStub blockingStub;

    public APIClient(@NonNull String target) {
        channel = NettyChannelBuilder.forTarget(target).usePlaintext().nameResolverFactory(new DnsNameResolverProvider()).build();
        stub = SysteraGrpc.newStub(channel);
        blockingStub = SysteraGrpc.newBlockingStub(channel);
    }

    // Utility Method
    public static SysteraProtos.SystemStream systemStreamFromJson(String jsonText) {
        try {
            SysteraProtos.SystemStream.Builder builder = SysteraProtos.SystemStream.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(jsonText, builder);
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    public static SysteraProtos.PlayerStream playerStreamFromJson(String jsonText) {
        try {
            SysteraProtos.PlayerStream.Builder builder = SysteraProtos.PlayerStream.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(jsonText, builder);
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    public static SysteraProtos.PunishmentStream punishmentStreamFromJson(String jsonText) {
        try {
            SysteraProtos.PunishmentStream.Builder builder = SysteraProtos.PunishmentStream.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(jsonText, builder);
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    public static SysteraProtos.GroupStream groupStreamFromJson(String jsonText) {
        try {
            SysteraProtos.GroupStream.Builder builder = SysteraProtos.GroupStream.newBuilder();
            JsonFormat.parser().ignoringUnknownFields().merge(jsonText, builder);
            return builder.build();
        } catch (Exception e) {
            return null;
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    private String toString(@NonNull UUID uuid) {
        return uuid.toString().replaceAll("-", "");
    }

    public static UUID toUUID(@NonNull String plain) {
        plain = plain.substring(0, 8) + "-" + plain.substring(8, 12) + "-" + plain.substring(12, 16) + "-" + plain.substring(16, 20) + "-" + plain.substring(20, 32);
        return UUID.fromString(plain);
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

    public CompletableFuture<SysteraProtos.Empty> dispatch(@NonNull String target, @NonNull String cmd) {
        DispatchRequest request = DispatchRequest.newBuilder()
                .setTarget(target)
                .setCmd(cmd)
                .build();

        CompletableFuture<SysteraProtos.Empty> future = new CompletableFuture<>();
        stub.dispatch(request, new CompletableFutureObserver<>(future));

        return future;
    }


    public CompletableFuture<Boolean> initPlayerProfile(@NonNull UUID uuid, @NonNull String name, @NonNull String ipAddress, @NonNull String hostname) {
        InitPlayerProfileRequest request = InitPlayerProfileRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .setPlayerName(name)
                .setPlayerIPAddress(ipAddress)
                .setPlayerHostname(hostname)
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
                .setForce(force)
                .setEntry(entry)
                .build();

        CompletableFuture<SysteraProtos.SetPlayerPunishResponse> future = new CompletableFuture<>();
        stub.setPlayerPunish(request, new CompletableFutureObserver<>(future));
        return future;
    }

    public CompletableFuture<SysteraProtos.ReportResponse> report(UUID fromUUID, String fromName, UUID toUUID, String toName, String message) {
        PlayerData from = PlayerData.newBuilder()
                .setUUID(toString(fromUUID))
                .setName(fromName)
                .build();

        PlayerData to = PlayerData.newBuilder()
                .setUUID(toString(toUUID))
                .setName(toName)
                .build();

        ReportRequest request = ReportRequest.newBuilder()
                .setFrom(from)
                .setTo(to)
                .setMessage(message)
                .build();

        CompletableFuture<SysteraProtos.ReportResponse> future = new CompletableFuture<>();
        stub.report(request, new CompletableFutureObserver<>(future));
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