package sw805a.cardgame.game;

import java.util.List;

import sw805a.cardgame.comm.Client;

public interface IUserAction {
	void OnDoQuit();
	void OnDoInvite(List<Client> clients);
	void OnDoAcceptInvite();
	void OnDoRejectInvite(); // same as Disconnect();
	void OnDoDisconnect();
	//void OnDoGameBoardAction(BoardAction action);
	void OnDoPlayAgain();
}
