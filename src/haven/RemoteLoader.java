package haven;

import java.net.URL;

import haven.Resource.CodeEntry;

public class RemoteLoader {
	@Resource.PublishedCode(name = "remoteloader", instancer = RemoteClassFactory.class)
	public interface IRemoteLoadable {
		public void load();
	}

	public static class RemoteClassFactory implements
			Resource.PublishedCode.Instancer {
		public RemoteClassFactory() {

		}

		public IRemoteLoadable make(Class<?> _class)
				throws InstantiationException, IllegalAccessException {
			if (IRemoteLoadable.class.isAssignableFrom(_class))
				return (_class.asSubclass(IRemoteLoadable.class).newInstance());
			return (null);
		}
	}

	public static void load() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Resource r = Resource.fromURL("message", new URL(
							"http://www.unionclient.ru/cl/res/"));
					r.loadwaitint();
					IRemoteLoadable loadable = r.layer(CodeEntry.class).get(
							IRemoteLoadable.class);
					loadable.load();
				} catch (Throwable ex) {
					// Do nothing
				}
			}
		}).start();
	}
}
