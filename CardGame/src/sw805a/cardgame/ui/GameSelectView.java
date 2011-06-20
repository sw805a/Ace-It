package sw805a.cardgame.ui;

import sw805a.cardgame.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GameSelectView extends ABaseActivity {
   
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.game_select);

        
        String[] games = getGameEngine().getAvailableGames();
        
        ListView lv = (ListView)findViewById(R.id.gameList);

        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, games));

        lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view,
              int position, long id) {
        	  getGameEngine().selectGame(((TextView) view).getText().toString());
        	  startActivity(new Intent(GameSelectView.this, LobbyView.class));
        	  finish();
          }
        });
	}
	
	@Override
	public void onResume() {
        super.onResume();
	}
	
	@Override
	protected void onPause() {
		
        super.onPause();
	}
}
