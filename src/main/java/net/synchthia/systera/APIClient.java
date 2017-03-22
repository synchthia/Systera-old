package net.synchthia.systera;

import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.internal.DnsNameResolverProvider;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.synchthia.api.systera.SysteraGrpc;
import net.synchthia.api.systera.SysteraProtos;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
        SysteraProtos.QuitStreamRequest request = SysteraProtos.QuitStreamRequest.newBuilder()
                .setName(name)
                .build();

        blockingStub.quitStream(request);
    }

    public void actionStream(@NonNull String name) {
        SysteraProtos.StreamRequest request = SysteraProtos.StreamRequest.newBuilder()
                .setName(name)
                .build();

        stub.actionStream(request, new StreamObserver<SysteraProtos.ActionStreamResponse>() {
            @Override
            public void onNext(SysteraProtos.ActionStreamResponse value) {
                switch (value.getType()) {
                    case DISPATCH:
                        SysteraPlugin.getInstance().getLogger().log(Level.INFO, "Incoming Command: " + value.getCmd());
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value.getCmd());
                        break;
                }
            }

            @SneakyThrows
            @Override
            public void onError(Throwable t) {
                Status status = Status.fromThrowable(t);

                if (status.getCause() instanceof StatusRuntimeException) {
                    SysteraPlugin.getInstance().getLogger().log(Level.SEVERE, "StatusRuntimeException Occurred! Retrying...");
                    Thread.sleep(3000L);
                    actionStream(name);
                    return;
                }

                if (status.getCause() instanceof IOException) {
                    SysteraPlugin.getInstance().getLogger().log(Level.SEVERE, "IOException Occurred! API Server down?");
                    actionStream(name);
                    return;
                }

                System.out.println("Trace ===");
                System.out.println(status.getCause());
                System.out.println(status.getCode());
                System.out.println(status.getDescription());
                System.out.println("Trace ===");
            }

            @Override
            public void onCompleted() {
                SysteraPlugin.getInstance().getLogger().log(Level.INFO, "ActionStream connection closed!");
            }
        });
    }

    public CompletableFuture<SysteraProtos.Empty> announce(@NonNull String target, @NonNull String message) {
        SysteraProtos.AnnounceRequest request = SysteraProtos.AnnounceRequest.newBuilder()
                .setTarget(target)
                .setMessage(message)
                .build();

        CompletableFuture<SysteraProtos.Empty> future = new CompletableFuture<>();
        stub.announce(request, new CompletableFutureObserver<>(future));

        return future;
    }

    public CompletableFuture<Boolean> initPlayerProfile(@NonNull UUID uuid, @NonNull String name, @NonNull String ipAddress) {
        SysteraProtos.InitPlayerProfileRequest request = SysteraProtos.InitPlayerProfileRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .setPlayerName(name)
                .setPlayerIPAddress(ipAddress)
                .build();

        CompletableFuture<SysteraProtos.InitPlayerProfileResponse> future = new CompletableFuture<>();
        stub.initPlayerProfile(request, new CompletableFutureObserver<>(future));

        return future.thenApply(SysteraProtos.InitPlayerProfileResponse::getHasProfile);
    }

    public CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfile(@NonNull UUID uuid) {
        SysteraProtos.FetchPlayerProfileRequest request = SysteraProtos.FetchPlayerProfileRequest.newBuilder()
                .setPlayerUUID(toString(uuid))
                .build();

        CompletableFuture<SysteraProtos.FetchPlayerProfileResponse> future = new CompletableFuture<>();
        stub.fetchPlayerProfile(request, new CompletableFutureObserver<>(future));

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