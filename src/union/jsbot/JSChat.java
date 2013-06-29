package union.jsbot;

import haven.ChatHW;
import haven.UI;

public class JSChat {
	private int remote_id;

	public JSChat(int rid) {
		remote_id = rid;
	}

	/**
	 * Возвращает название чата
	 * 
	 * @return Название чата
	 */
	public String chatName() {
		return wdg().title;
	}

	/**
	 * Посылает сообщение
	 * 
	 * @param message
	 *            Сообщение для отправки
	 */
	public void sendMessage(String message) {
		wdg().sendmsg(message);
	}

	/**
	 * Проверяет есть ли новое сообщение в чате
	 * 
	 * @return true если есть непрочитанное сообщение
	 */
	public boolean haveNewMessage() {
		return wdg().hasNewMessage();
	}
	
	/**
	 * Закрывает текущий чат
	 */
	public void closeChat() {
		wdg().closeChat();
	}

	/**
	 * Возвращает посленее пришедшее сообщение
	 * 
	 * @return Строка сообщения
	 */
	public String getLastMessage() {
		return wdg().getLastMessage();
	}

	private ChatHW wdg() {
		return (ChatHW) UI.instance.getWidget(remote_id);
	}

	/**
	 * Проверяет, существует ли еще объект
	 * 
	 * @return true если объект существует
	 */
	public boolean isActual() {
		return wdg() != null;
	}
}