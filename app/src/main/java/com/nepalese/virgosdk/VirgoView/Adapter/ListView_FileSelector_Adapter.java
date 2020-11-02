package com.nepalese.virgosdk.VirgoView.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nepalese.virgosdk.Beans.CheckBean;
import com.nepalese.virgosdk.R;
import com.nepalese.virgosdk.Util.MediaUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListView_FileSelector_Adapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<File> data;
    private List<CheckBean> beans = new ArrayList<>();//记录checkbox的选中情况
    private FileInterListener interListener;//供外部引用接口

    public ListView_FileSelector_Adapter(Context context, List<File> data, FileInterListener interListener){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.interListener = interListener;
        for (int i = 0; i < data.size(); i++) {
            CheckBean bean = new CheckBean(i, false);
            beans.add(bean);
        }
    }
    @Override
    public int getCount() {
        return data==null ? 0:data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        public LinearLayout layout;
        public TextView tvData;
        public ImageView imageView;
        public CheckBox checkBox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_file_selector_list, null);
            holder = new ViewHolder();

            holder.layout = convertView.findViewById(R.id.layout_all);
            holder.tvData = convertView.findViewById(R.id.tvFilePath);
            holder.imageView = convertView.findViewById(R.id.imgFileDir);
            holder.checkBox = convertView.findViewById(R.id.cbChoose);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.layout.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        String path = data.get(position).getPath();
        holder.tvData.setText(path.substring(path.lastIndexOf("/")+1));//show the last layer
        if(data.get(position).isDirectory()){
            holder.imageView.setImageResource(R.drawable.icon_dir);
        }else{
            //file
            String tail = path.substring(path.lastIndexOf(".")+1);
            switch(tail.toLowerCase()){
                case "mp3":
                case "wav":
                case "mp4":
                    holder.imageView.setImageBitmap(MediaUtil.parseAlbum(context, data.get(position), R.drawable.icon_media));
                    break;
                case "jpg":
                case "png":
                    holder.imageView.setImageBitmap(MediaUtil.getImageThumbnail(context, path));
                    break;
                default:
                    holder.imageView.setImageResource(R.drawable.icon_file);
                    break;
            }
        }

        //make component be able click from outside
        //防止CheckBox因滚动ListView时混乱
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beans.get(position).setChecked(!beans.get(position).isChecked());
                interListener.itemClick(view, beans.get(position).isChecked());
                holder.checkBox.setChecked(beans.get(position).isChecked());
            }
        });

//        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                beans.get(position).setChecked(b);
//                interListener.itemClick(compoundButton, b);
//            }
//        });

        holder.checkBox.setChecked(beans.get(position).isChecked());
        holder.checkBox.setTag(position);
        return convertView;
    }

    public interface FileInterListener {
        void itemClick(View v, boolean isChecked);
    }
}
