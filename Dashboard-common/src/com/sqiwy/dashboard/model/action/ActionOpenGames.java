package com.sqiwy.dashboard.model.action;

import com.sqiwy.dashboard.DBGamesActivity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

import com.sqiwy.dashboard.R;


/**
 * Created by abrysov on 4/18/14.
 */
public class ActionOpenGames extends ActionBase {

    @Override
    public void execute(ActionContext actionContext) {
        super.execute(actionContext);

        Context context = actionContext.getContext();
        Intent intent = new Intent(context, DBGamesActivity.class);
        ActivityOptions options = ActivityOptions.makeCustomAnimation(context, R.anim.move_down_enter, R.anim.move_down_exit);

        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, REQUEST_CODE, options.toBundle());
        }
        else {
            context.startActivity(intent, options.toBundle());
        }

        onActionDone();
    }

    @Override
    public String getType() {
        return Action.TYPE_OPEN_GAMES;
    }

    @Override
    public void init(JSONObject data) throws JSONException {
        super.init(data);
    }


}
