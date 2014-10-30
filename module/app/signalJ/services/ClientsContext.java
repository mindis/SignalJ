package signalJ.services;
import java.util.UUID;

import akka.actor.ActorRef;
import play.Logger;
import signalJ.models.RequestContext;
import signalJ.services.HubActor.ClientFunctionCall.SendType;

public class ClientsContext<S> {
	public final S all;
	public final S others;
	public final S caller;
	private final Class<S> clazz;
	private final RequestContext context;
    private final ActorRef signalJActor;
    private final String hubName;

    @SuppressWarnings("unchecked")
	ClientsContext(Class<S> clazz, String hubName, RequestContext context, ActorRef signalJActor) {
        Logger.debug("ClientsContext: " + hubName);
        this.clazz = clazz;
		this.context = context;
		this.all = (S) new SenderProxy(signalJActor, SendType.All, clazz, hubName, context).createProxy();
		this.others = (S) new SenderProxy(signalJActor, SendType.Others, clazz, hubName, context).createProxy();
		this.caller = (S) new SenderProxy(signalJActor, SendType.Caller, clazz, hubName, context).createProxy();
        this.signalJActor = signalJActor;
        this.hubName = hubName;
	}

    @SuppressWarnings("unchecked")
	public S client(UUID... connectionIds) {
		return (S) new SenderProxy(signalJActor, SendType.Clients, clazz, hubName, context, connectionIds, (UUID[])null, null).createProxy();
	}
	
	@SuppressWarnings("unchecked")
	public S allExcept(UUID... connectionIds) {
		return (S) new SenderProxy(signalJActor, SendType.AllExcept, clazz, hubName, context, (UUID[])null, connectionIds, null).createProxy();
	}
	
	@SuppressWarnings("unchecked")
	public S group(String groupName) {
		return (S) new SenderProxy(signalJActor, SendType.Group, clazz, hubName, context, (UUID[])null, (UUID[])null, groupName).createProxy();
	}
	
	@SuppressWarnings("unchecked")
	public S group(String groupName, UUID... connectionIds) {
		return (S) new SenderProxy(signalJActor, SendType.InGroupExcept, clazz, hubName, context, (UUID[])null, connectionIds, groupName).createProxy();
	}
	
	public S inGroupExcept(String groupName, UUID... connectionIds) {
		return group(groupName, connectionIds);
	}
	
	@SuppressWarnings("unchecked")
	public S othersInGroup(String groupName) {
		UUID[] uuids = new UUID[1];
		uuids[0] = context.connectionId;
		return (S) new SenderProxy(signalJActor, SendType.InGroupExcept, clazz, hubName, context, (UUID[])null, uuids, groupName).createProxy();
	}
}