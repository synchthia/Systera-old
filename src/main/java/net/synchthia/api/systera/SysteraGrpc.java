package net.synchthia.api.systera;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 * <pre>
 * Systera API
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.0.2)",
    comments = "Source: systera.proto")
public class SysteraGrpc {

  private SysteraGrpc() {}

  public static final String SERVICE_NAME = "apipb.Systera";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.StreamRequest,
      net.synchthia.api.systera.SysteraProtos.ActionStreamResponse> METHOD_ACTION_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING,
          generateFullMethodName(
              "apipb.Systera", "ActionStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.StreamRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.ActionStreamResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.AnnounceRequest,
      net.synchthia.api.systera.SysteraProtos.Empty> METHOD_ANNOUNCE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "apipb.Systera", "Announce"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.AnnounceRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.QuitStreamRequest,
      net.synchthia.api.systera.SysteraProtos.Empty> METHOD_QUIT_STREAM =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "apipb.Systera", "QuitStream"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.QuitStreamRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.InitPlayerProfileRequest,
      net.synchthia.api.systera.SysteraProtos.InitPlayerProfileResponse> METHOD_INIT_PLAYER_PROFILE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "apipb.Systera", "InitPlayerProfile"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.InitPlayerProfileRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.InitPlayerProfileResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileRequest,
      net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileResponse> METHOD_FETCH_PLAYER_PROFILE =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "apipb.Systera", "FetchPlayerProfile"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.FetchGroupsRequest,
      net.synchthia.api.systera.SysteraProtos.FetchGroupsResponse> METHOD_FETCH_GROUPS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "apipb.Systera", "FetchGroups"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.FetchGroupsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.FetchGroupsResponse.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.SetPlayerServerRequest,
      net.synchthia.api.systera.SysteraProtos.Empty> METHOD_SET_PLAYER_SERVER =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "apipb.Systera", "SetPlayerServer"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.SetPlayerServerRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.RemovePlayerServerRequest,
      net.synchthia.api.systera.SysteraProtos.Empty> METHOD_REMOVE_PLAYER_SERVER =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "apipb.Systera", "RemovePlayerServer"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.RemovePlayerServerRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.Empty.getDefaultInstance()));
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<net.synchthia.api.systera.SysteraProtos.SetPlayerSettingsRequest,
      net.synchthia.api.systera.SysteraProtos.Empty> METHOD_SET_PLAYER_SETTINGS =
      io.grpc.MethodDescriptor.create(
          io.grpc.MethodDescriptor.MethodType.UNARY,
          generateFullMethodName(
              "apipb.Systera", "SetPlayerSettings"),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.SetPlayerSettingsRequest.getDefaultInstance()),
          io.grpc.protobuf.ProtoUtils.marshaller(net.synchthia.api.systera.SysteraProtos.Empty.getDefaultInstance()));

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static SysteraStub newStub(io.grpc.Channel channel) {
    return new SysteraStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static SysteraBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new SysteraBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary and streaming output calls on the service
   */
  public static SysteraFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new SysteraFutureStub(channel);
  }

  /**
   * <pre>
   * Systera API
   * </pre>
   */
  public static abstract class SysteraImplBase implements io.grpc.BindableService {

    /**
     */
    public void actionStream(net.synchthia.api.systera.SysteraProtos.StreamRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.ActionStreamResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ACTION_STREAM, responseObserver);
    }

    /**
     */
    public void announce(net.synchthia.api.systera.SysteraProtos.AnnounceRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_ANNOUNCE, responseObserver);
    }

    /**
     */
    public void quitStream(net.synchthia.api.systera.SysteraProtos.QuitStreamRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_QUIT_STREAM, responseObserver);
    }

    /**
     */
    public void initPlayerProfile(net.synchthia.api.systera.SysteraProtos.InitPlayerProfileRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.InitPlayerProfileResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_INIT_PLAYER_PROFILE, responseObserver);
    }

    /**
     */
    public void fetchPlayerProfile(net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_FETCH_PLAYER_PROFILE, responseObserver);
    }

    /**
     */
    public void fetchGroups(net.synchthia.api.systera.SysteraProtos.FetchGroupsRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.FetchGroupsResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_FETCH_GROUPS, responseObserver);
    }

    /**
     */
    public void setPlayerServer(net.synchthia.api.systera.SysteraProtos.SetPlayerServerRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_PLAYER_SERVER, responseObserver);
    }

    /**
     */
    public void removePlayerServer(net.synchthia.api.systera.SysteraProtos.RemovePlayerServerRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_REMOVE_PLAYER_SERVER, responseObserver);
    }

    /**
     */
    public void setPlayerSettings(net.synchthia.api.systera.SysteraProtos.SetPlayerSettingsRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_SET_PLAYER_SETTINGS, responseObserver);
    }

    @java.lang.Override public io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_ACTION_STREAM,
            asyncServerStreamingCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.StreamRequest,
                net.synchthia.api.systera.SysteraProtos.ActionStreamResponse>(
                  this, METHODID_ACTION_STREAM)))
          .addMethod(
            METHOD_ANNOUNCE,
            asyncUnaryCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.AnnounceRequest,
                net.synchthia.api.systera.SysteraProtos.Empty>(
                  this, METHODID_ANNOUNCE)))
          .addMethod(
            METHOD_QUIT_STREAM,
            asyncUnaryCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.QuitStreamRequest,
                net.synchthia.api.systera.SysteraProtos.Empty>(
                  this, METHODID_QUIT_STREAM)))
          .addMethod(
            METHOD_INIT_PLAYER_PROFILE,
            asyncUnaryCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.InitPlayerProfileRequest,
                net.synchthia.api.systera.SysteraProtos.InitPlayerProfileResponse>(
                  this, METHODID_INIT_PLAYER_PROFILE)))
          .addMethod(
            METHOD_FETCH_PLAYER_PROFILE,
            asyncUnaryCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileRequest,
                net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileResponse>(
                  this, METHODID_FETCH_PLAYER_PROFILE)))
          .addMethod(
            METHOD_FETCH_GROUPS,
            asyncUnaryCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.FetchGroupsRequest,
                net.synchthia.api.systera.SysteraProtos.FetchGroupsResponse>(
                  this, METHODID_FETCH_GROUPS)))
          .addMethod(
            METHOD_SET_PLAYER_SERVER,
            asyncUnaryCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.SetPlayerServerRequest,
                net.synchthia.api.systera.SysteraProtos.Empty>(
                  this, METHODID_SET_PLAYER_SERVER)))
          .addMethod(
            METHOD_REMOVE_PLAYER_SERVER,
            asyncUnaryCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.RemovePlayerServerRequest,
                net.synchthia.api.systera.SysteraProtos.Empty>(
                  this, METHODID_REMOVE_PLAYER_SERVER)))
          .addMethod(
            METHOD_SET_PLAYER_SETTINGS,
            asyncUnaryCall(
              new MethodHandlers<
                net.synchthia.api.systera.SysteraProtos.SetPlayerSettingsRequest,
                net.synchthia.api.systera.SysteraProtos.Empty>(
                  this, METHODID_SET_PLAYER_SETTINGS)))
          .build();
    }
  }

  /**
   * <pre>
   * Systera API
   * </pre>
   */
  public static final class SysteraStub extends io.grpc.stub.AbstractStub<SysteraStub> {
    private SysteraStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SysteraStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SysteraStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SysteraStub(channel, callOptions);
    }

    /**
     */
    public void actionStream(net.synchthia.api.systera.SysteraProtos.StreamRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.ActionStreamResponse> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_ACTION_STREAM, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void announce(net.synchthia.api.systera.SysteraProtos.AnnounceRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_ANNOUNCE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void quitStream(net.synchthia.api.systera.SysteraProtos.QuitStreamRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_QUIT_STREAM, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void initPlayerProfile(net.synchthia.api.systera.SysteraProtos.InitPlayerProfileRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.InitPlayerProfileResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_INIT_PLAYER_PROFILE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fetchPlayerProfile(net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FETCH_PLAYER_PROFILE, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void fetchGroups(net.synchthia.api.systera.SysteraProtos.FetchGroupsRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.FetchGroupsResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_FETCH_GROUPS, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setPlayerServer(net.synchthia.api.systera.SysteraProtos.SetPlayerServerRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_PLAYER_SERVER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void removePlayerServer(net.synchthia.api.systera.SysteraProtos.RemovePlayerServerRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_REMOVE_PLAYER_SERVER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void setPlayerSettings(net.synchthia.api.systera.SysteraProtos.SetPlayerSettingsRequest request,
        io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_SET_PLAYER_SETTINGS, getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * Systera API
   * </pre>
   */
  public static final class SysteraBlockingStub extends io.grpc.stub.AbstractStub<SysteraBlockingStub> {
    private SysteraBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SysteraBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SysteraBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SysteraBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<net.synchthia.api.systera.SysteraProtos.ActionStreamResponse> actionStream(
        net.synchthia.api.systera.SysteraProtos.StreamRequest request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_ACTION_STREAM, getCallOptions(), request);
    }

    /**
     */
    public net.synchthia.api.systera.SysteraProtos.Empty announce(net.synchthia.api.systera.SysteraProtos.AnnounceRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_ANNOUNCE, getCallOptions(), request);
    }

    /**
     */
    public net.synchthia.api.systera.SysteraProtos.Empty quitStream(net.synchthia.api.systera.SysteraProtos.QuitStreamRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_QUIT_STREAM, getCallOptions(), request);
    }

    /**
     */
    public net.synchthia.api.systera.SysteraProtos.InitPlayerProfileResponse initPlayerProfile(net.synchthia.api.systera.SysteraProtos.InitPlayerProfileRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_INIT_PLAYER_PROFILE, getCallOptions(), request);
    }

    /**
     */
    public net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileResponse fetchPlayerProfile(net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FETCH_PLAYER_PROFILE, getCallOptions(), request);
    }

    /**
     */
    public net.synchthia.api.systera.SysteraProtos.FetchGroupsResponse fetchGroups(net.synchthia.api.systera.SysteraProtos.FetchGroupsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_FETCH_GROUPS, getCallOptions(), request);
    }

    /**
     */
    public net.synchthia.api.systera.SysteraProtos.Empty setPlayerServer(net.synchthia.api.systera.SysteraProtos.SetPlayerServerRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_PLAYER_SERVER, getCallOptions(), request);
    }

    /**
     */
    public net.synchthia.api.systera.SysteraProtos.Empty removePlayerServer(net.synchthia.api.systera.SysteraProtos.RemovePlayerServerRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_REMOVE_PLAYER_SERVER, getCallOptions(), request);
    }

    /**
     */
    public net.synchthia.api.systera.SysteraProtos.Empty setPlayerSettings(net.synchthia.api.systera.SysteraProtos.SetPlayerSettingsRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_SET_PLAYER_SETTINGS, getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * Systera API
   * </pre>
   */
  public static final class SysteraFutureStub extends io.grpc.stub.AbstractStub<SysteraFutureStub> {
    private SysteraFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private SysteraFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected SysteraFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new SysteraFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.synchthia.api.systera.SysteraProtos.Empty> announce(
        net.synchthia.api.systera.SysteraProtos.AnnounceRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_ANNOUNCE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.synchthia.api.systera.SysteraProtos.Empty> quitStream(
        net.synchthia.api.systera.SysteraProtos.QuitStreamRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_QUIT_STREAM, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.synchthia.api.systera.SysteraProtos.InitPlayerProfileResponse> initPlayerProfile(
        net.synchthia.api.systera.SysteraProtos.InitPlayerProfileRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_INIT_PLAYER_PROFILE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileResponse> fetchPlayerProfile(
        net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FETCH_PLAYER_PROFILE, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.synchthia.api.systera.SysteraProtos.FetchGroupsResponse> fetchGroups(
        net.synchthia.api.systera.SysteraProtos.FetchGroupsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_FETCH_GROUPS, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.synchthia.api.systera.SysteraProtos.Empty> setPlayerServer(
        net.synchthia.api.systera.SysteraProtos.SetPlayerServerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_PLAYER_SERVER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.synchthia.api.systera.SysteraProtos.Empty> removePlayerServer(
        net.synchthia.api.systera.SysteraProtos.RemovePlayerServerRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_REMOVE_PLAYER_SERVER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<net.synchthia.api.systera.SysteraProtos.Empty> setPlayerSettings(
        net.synchthia.api.systera.SysteraProtos.SetPlayerSettingsRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_SET_PLAYER_SETTINGS, getCallOptions()), request);
    }
  }

  private static final int METHODID_ACTION_STREAM = 0;
  private static final int METHODID_ANNOUNCE = 1;
  private static final int METHODID_QUIT_STREAM = 2;
  private static final int METHODID_INIT_PLAYER_PROFILE = 3;
  private static final int METHODID_FETCH_PLAYER_PROFILE = 4;
  private static final int METHODID_FETCH_GROUPS = 5;
  private static final int METHODID_SET_PLAYER_SERVER = 6;
  private static final int METHODID_REMOVE_PLAYER_SERVER = 7;
  private static final int METHODID_SET_PLAYER_SETTINGS = 8;

  private static class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final SysteraImplBase serviceImpl;
    private final int methodId;

    public MethodHandlers(SysteraImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_ACTION_STREAM:
          serviceImpl.actionStream((net.synchthia.api.systera.SysteraProtos.StreamRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.ActionStreamResponse>) responseObserver);
          break;
        case METHODID_ANNOUNCE:
          serviceImpl.announce((net.synchthia.api.systera.SysteraProtos.AnnounceRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty>) responseObserver);
          break;
        case METHODID_QUIT_STREAM:
          serviceImpl.quitStream((net.synchthia.api.systera.SysteraProtos.QuitStreamRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty>) responseObserver);
          break;
        case METHODID_INIT_PLAYER_PROFILE:
          serviceImpl.initPlayerProfile((net.synchthia.api.systera.SysteraProtos.InitPlayerProfileRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.InitPlayerProfileResponse>) responseObserver);
          break;
        case METHODID_FETCH_PLAYER_PROFILE:
          serviceImpl.fetchPlayerProfile((net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.FetchPlayerProfileResponse>) responseObserver);
          break;
        case METHODID_FETCH_GROUPS:
          serviceImpl.fetchGroups((net.synchthia.api.systera.SysteraProtos.FetchGroupsRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.FetchGroupsResponse>) responseObserver);
          break;
        case METHODID_SET_PLAYER_SERVER:
          serviceImpl.setPlayerServer((net.synchthia.api.systera.SysteraProtos.SetPlayerServerRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty>) responseObserver);
          break;
        case METHODID_REMOVE_PLAYER_SERVER:
          serviceImpl.removePlayerServer((net.synchthia.api.systera.SysteraProtos.RemovePlayerServerRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty>) responseObserver);
          break;
        case METHODID_SET_PLAYER_SETTINGS:
          serviceImpl.setPlayerSettings((net.synchthia.api.systera.SysteraProtos.SetPlayerSettingsRequest) request,
              (io.grpc.stub.StreamObserver<net.synchthia.api.systera.SysteraProtos.Empty>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    return new io.grpc.ServiceDescriptor(SERVICE_NAME,
        METHOD_ACTION_STREAM,
        METHOD_ANNOUNCE,
        METHOD_QUIT_STREAM,
        METHOD_INIT_PLAYER_PROFILE,
        METHOD_FETCH_PLAYER_PROFILE,
        METHOD_FETCH_GROUPS,
        METHOD_SET_PLAYER_SERVER,
        METHOD_REMOVE_PLAYER_SERVER,
        METHOD_SET_PLAYER_SETTINGS);
  }

}
