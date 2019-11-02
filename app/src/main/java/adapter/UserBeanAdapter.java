/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.feedback.Activity_Login;
import com.example.feedback.R;
import java.util.ArrayList;
import java.util.List;

import bean.UserBean;

public class UserBeanAdapter extends BaseAdapter {

    private Context mContext;

    private List<UserBean> mUserBeanList;

    public UserBeanAdapter(Context context){
        mUserBeanList = new ArrayList<>();
        mContext = context;
    }

    public void replaceData(@NonNull List<UserBean> userBeans) {
        mUserBeanList.clear();
        mUserBeanList.addAll(userBeans);
    }

    @Override
    public int getCount() {
        return mUserBeanList.size();
    }

    @Override
    public UserBean getItem(int position) {
        return mUserBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = View.inflate(mContext, R.layout.item_user_bean,null);

            holder = new ViewHolder();
            holder.mAccountView = convertView.findViewById(R.id.tv_item_account);
            holder.mClearView = convertView.findViewById(R.id.iv_item_clear_account);
            holder.mClearView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserBean checkedUser = getItem(position);
                    String email = checkedUser.getAccount();
                    String password = checkedUser.getPassword();
                    mUserBeanList.remove(position);
                    notifyDataSetChanged();
                    Activity_Login.mUserInfoOpertor.deleteUserInfo(email, password);
                }
            });
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        UserBean user = getItem(position);
        holder.mAccountView.setText(user.getAccount());

        return convertView;
    }

    private class ViewHolder{
        private TextView mAccountView;
        private ImageView mClearView;
    }
}
