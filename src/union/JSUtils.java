package union;

import org.mozilla.javascript.Context;

public class JSUtils {
	public static class StoppableContext extends Context {
		private boolean stop = false;

		@SuppressWarnings("deprecation")
		public StoppableContext() {
			enter();
			setGenerateObserverCount(true);
		}

		@Override
		protected void observeInstructionCount(int instructionCount) {
			if (stop) {
				throw new StoppedExecutionException();
			}
		}

		public void Stop() {
			stop = true;
		}
	}

	public static class StoppedExecutionException extends RuntimeException {
		private static final long serialVersionUID = 73977746046647385L;

		public StoppedExecutionException() {
		};
	}
}
