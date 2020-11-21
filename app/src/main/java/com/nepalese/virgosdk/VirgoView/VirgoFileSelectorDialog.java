package com.nepalese.virgosdk.VirgoView;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nepalese.virgosdk.R;
import com.nepalese.virgosdk.Util.SystemUtil;
import com.nepalese.virgosdk.VirgoView.Adapter.ListView_FileSelector_Adapter;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author nepalese on 2020/10/30 15:20
 * @usage 弹框式文件选择器，可筛选文件类型或仅文件夹，返回选中的文件或文件夹
 *
 */
public class VirgoFileSelectorDialog extends Dialog implements ListView_FileSelector_Adapter.FileInterListener {
    private static final String TAG = "FileSelectorDialog";

    //选择文件或文件夹
    public static final int FLAG_DIR = 0;
    public static final int FLAG_FILE = 1;

    //仅显示某种特定文件
    public static final String TYPE_ALL = "all";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_TEXT = "txt";
    public static final String TYPE_VIDEO = "video";
    public static final String TYPE_AUDIO = "audio";

    //选择特定文件类型后将显示的文件拓展名
    public static final String[] IMAGE_EXTENSION = {"jpg", "jpeg", "png", "svg", "bmp", "tiff"};
    public static final String[] AUDIO_EXTENSION = {"mp3", "wav", "wma", "aac", "flac"};
    public static final String[] VIDEO_EXTENSION = {"mp4", "flv", "avi", "wmv", "mpeg", "mov", "rm", "swf"};
    public static final String[] TEXT_EXTENSION = {"txt", "java", "html", "xml", "php"};

    private static final String DEFAULT_ROOT_PATH = "/storage/emulated/0";//默认初始位置

    private Context context;
    private SelectFileCallback callback;

    private TextView tvCurPath, tvConfirm;
    private LinearLayout layoutRoot, layoutLast;
    private ListView listView;
    private ListView_FileSelector_Adapter adapter;

    private String curPath;//当前路径
    private List<File> files;//返回值
    private List<Integer> index;//选中文件、夹索引

    //默认设置
    private int flag = FLAG_FILE;//默认选择文件
    private String rootPath = DEFAULT_ROOT_PATH;
    private String fileType = TYPE_ALL;
    private int dialogHeight = 500;//默认弹框高度

    public VirgoFileSelectorDialog(@NonNull Context context) {
        //默认自定义弹框样式，使用下面的构造函数可另设样式
        this(context, R.style.File_Dialog);
    }

    public VirgoFileSelectorDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        setCancelable(false);
        init(context);
    }

    private void init(Context context) {
        this.context = context;

        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        View view = mLayoutInflater.inflate(R.layout.layout_file_selector, null);

        tvCurPath = view.findViewById(R.id.tvCurPath);
        tvConfirm = view.findViewById(R.id.tvConfirmChoose);

        layoutRoot = view.findViewById(R.id.layoutToRoot);
        layoutLast = view.findViewById(R.id.layoutToLast);

        listView = view.findViewById(R.id.listViewFile);
        index = new ArrayList<>();

        setContentView(view);
        setListener();
    }

    private void setLayout() {
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);

        /*
         * lp.x与lp.y表示相对于原始位置的偏移.
         * 当参数值包含Gravity.LEFT时,对话框出现在左边,所以lp.x就表示相对左边的偏移,负值忽略.
         * 当参数值包含Gravity.RIGHT时,对话框出现在右边,所以lp.x就表示相对右边的偏移,负值忽略.
         * 当参数值包含Gravity.TOP时,对话框出现在上边,所以lp.y就表示相对上边的偏移,负值忽略.
         * 当参数值包含Gravity.BOTTOM时,对话框出现在下边,所以lp.y就表示相对下边的偏移,负值忽略.
         * 当参数值包含Gravity.CENTER_HORIZONTAL时
         * ,对话框水平居中,所以lp.x就表示在水平居中的位置移动lp.x像素,正值向右移动,负值向左移动.
         * 当参数值包含Gravity.CENTER_VERTICAL时
         * ,对话框垂直居中,所以lp.y就表示在垂直居中的位置移动lp.y像素,正值向右移动,负值向左移动.
         * gravity的默认值为Gravity.CENTER,即Gravity.CENTER_HORIZONTAL |
         * Gravity.CENTER_VERTICAL.
         */

//        lp.width = 300; // 宽度
        lp.height = dialogHeight; // 高度
        lp.alpha = 0.8f; // 透明度

        dialogWindow.setAttributes(lp);
    }

    //==============================================================================================
    //初始化数据
    private void setData() {
        setLayout();//设置弹框布局
        curPath = rootPath;
        tvCurPath.setText(curPath);
        files  = new ArrayList(getFiles(curPath));

        adapter = new ListView_FileSelector_Adapter(context, files, this);//指向的是最开始的list
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);
    }

    //进入新的路径或返回上一层，刷新数据
    private void resetData(String path){
        //若为空文件夹，则不进入
        File file = new File(path);
        if(file.listFiles()==null || file.listFiles().length==0){
            SystemUtil.showToast(context, "空文件夹！");
            return;
        }
        curPath = path;
        tvCurPath.setText(curPath);
        files.clear();
        index.clear();//重置选中

        //note: 若直接使用 files = getFiles(curPath);listView 将无变化
        List<File> temp = getFiles(curPath);
        files.addAll(temp);
        adapter.notifyDataSetChanged();
    }

    //根据条件筛选显示文件
    private List<File> getFiles(String path){
        if(flag==FLAG_DIR){
            FileFilter filter = File::isDirectory;//File::isDirectory
            return Arrays.asList(Objects.requireNonNull(new File(path).listFiles(filter)));
        }else if(flag==FLAG_FILE){//显示所有文件夹及选择的类型的文件
            switch (fileType){
                case TYPE_ALL:
                    File[] fs = new File(path).listFiles();
                    assert fs != null;
                    Arrays.sort(fs, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                    return Arrays.asList(fs);
                case TYPE_AUDIO:
                    return getCertainFile(path, AUDIO_EXTENSION);
                case TYPE_IMAGE:
                    return getCertainFile(path, IMAGE_EXTENSION);
                case TYPE_TEXT:
                    return getCertainFile(path, TEXT_EXTENSION);
                case TYPE_VIDEO:
                    return getCertainFile(path, VIDEO_EXTENSION);
            }
        }
        return new ArrayList<>();
    }

    private List<File> getCertainFile(String path, String[] extension){
        List<File> list = new ArrayList<>();
        File[] files = new File(path).listFiles();;

        //排序
        Arrays.sort(files, (o1, o2) -> o1.getName().compareTo(o2.getName()));

        List extList = Arrays.asList(extension);
        for (File file:files){
            if (file.isDirectory()){
                list.add(file);
                continue;
            }
            if (extList.contains(getExtensionName(file.getName()))){
                list.add(file);
            }
        }
        return list;
    }

    private String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot > -1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1).toLowerCase();
            }
        }
        return "";
    }

    //清空数据
    private void release() {
        files.clear();
        index.clear();
        adapter = null;
    }

    //===============================================外部调用api=====================================
    @Override
    public void show() {
        super.show();
        setData();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        release();
    }

    //设置根目录
    public void setRootPath(String rootPath) {
        File file = new File(rootPath);
        if(file.exists() && file.isDirectory()){
            this.rootPath = rootPath;
        }
    }

    //设置选择文件或文件夹
    public void setFlag(int flag) {
        this.flag = flag;
    }

    //设置要筛选文件类型
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void setCallback(SelectFileCallback callback) {
        this.callback = callback;
    }

    public void setDialogHeight(int dialogHeight) {
        this.dialogHeight = dialogHeight;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            this.cancel();
        }
        return super.onKeyDown(keyCode, event);
    }

    //添加或移除选择对象
    @Override
    public void itemClick(View v, boolean isChecked) {
        Integer position = (Integer) v.getTag();
        if (v.getId() == R.id.cbChoose) {
            if (isChecked) {
                index.add(position);
                Log.i(TAG, "add: " + position);
            } else {
                index.remove(position);
                Log.i(TAG, "remove: " + position);
            }
        }
    }

    private void setListener() {
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("click", String.valueOf(position+1));
            //judge file/dir
            if(files.get(position).isFile()){
                //do nothing
                //可增加本地打开查看
            }else{
                //点击，进入文件夹
                resetData(files.get(position).getPath());
            }
        });

        tvConfirm.setOnClickListener(v -> {
            if(index.size()<1) {
                //未选择, 退出
                dismiss();
                return;
            }

            List<File> result = new ArrayList<>();
            switch (flag){
                case FLAG_DIR://return dirs
                    for(int i=0; i<index.size(); i++){
                        //双重保险
                        if(files.get(index.get(i)).isDirectory()){
                            result.add(files.get(index.get(i)));
                        }
                    }
                    break;
                case FLAG_FILE://return files
                    for(int i=0; i<index.size(); i++){
                        if(files.get(index.get(i)).isFile()){
                            result.add(files.get(index.get(i)));
                        }
                    }
                    break;
            }
            //通过回调函数返回结果
            if(callback!=null){
                callback.onResult(result);
            }
            //退出
            dismiss();
        });

        //返回上一级
        layoutLast.setOnClickListener(v -> {
            //judge curPath is root or not
            if (curPath.equals(rootPath)) {
                //do nothing
                SystemUtil.showToast(context, "已是根目录");
            }else{
                //back to last layer
                resetData(curPath.substring(0, curPath.lastIndexOf("/")));
            }
        });

        //返回根目录
        layoutRoot.setOnClickListener(v -> {
            if(curPath.equals(rootPath)){
                //do nothing
                SystemUtil.showToast(context, "已是根目录");
            }else{
                //back to root
                resetData(rootPath);
            }
        });
    }

    //自定义回调接口：
    public interface SelectFileCallback{
        void onResult(List<File> list);
    }
}
