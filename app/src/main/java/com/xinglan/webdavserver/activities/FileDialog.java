package com.xinglan.webdavserver.activities;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xinglan.webdavserver.R;

import org.apache.log4j.varia.ExternallyRolledFileAppender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class FileDialog extends ListActivity {
    public static final String CAN_SELECT_DIR = "CAN_SELECT_DIR";
    public static final String FORMAT_FILTER = "FORMAT_FILTER";
    private static final String ITEM_IMAGE = "image";
    private static final String ITEM_KEY = "key";
    public static final String RESULT_PATH = "RESULT_PATH";
    private static final String ROOT = "/";
    public static final String SELECTION_MODE = "SELECTION_MODE";
    public static final String SHOW_FOLDERS_ONLY = "SHOW_FOLDERS_ONLY";
    public static final String START_PATH = "START_PATH";
    private InputMethodManager inputManager;
    private LinearLayout layoutCreate;
    private LinearLayout layoutSelect;
    private EditText mFileName;
    private ArrayList<HashMap<String, Object>> mList;
    private TextView myPath;
    private String parentPath;
    private Button selectButton;
    private File selectedFile;
    private List<String> path = null;
    private String currentPath = "/";
    private int selectionMode = 0;
    private String[] formatFilter = null;
    private boolean canSelectDir = false;
    private boolean showFoldersOnly = false;
    private HashMap<String, Integer> lastPositions = new HashMap<>();

    @Override // android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(0, getIntent());
        setContentView(R.layout.file_dialog_main);
        this.myPath = findViewById(R.id.path);
        this.mFileName = findViewById(R.id.fdEditTextFile);
        this.inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        this.selectButton = findViewById(R.id.fdButtonSelect);
        this.selectButton.setEnabled(false);
        // android.view.View.OnClickListener
        this.selectButton.setOnClickListener(v -> {
            if (FileDialog.this.selectedFile != null) {
                FileDialog.this.getIntent().putExtra(FileDialog.RESULT_PATH, FileDialog.this.selectedFile.getPath());
                FileDialog.this.setResult(-1, FileDialog.this.getIntent());
                FileDialog.this.finish();
            }
        });
        Button newButton = findViewById(R.id.fdButtonNew);
        // android.view.View.OnClickListener
        newButton.setOnClickListener(v -> {
            FileDialog.this.setCreateVisible(v);
            FileDialog.this.mFileName.setText("");
            FileDialog.this.mFileName.requestFocus();
        });
        this.selectionMode = getIntent().getIntExtra(SELECTION_MODE, 0);
        this.formatFilter = getIntent().getStringArrayExtra(FORMAT_FILTER);
        this.canSelectDir = getIntent().getBooleanExtra(CAN_SELECT_DIR, false);
        this.showFoldersOnly = getIntent().getBooleanExtra(SHOW_FOLDERS_ONLY, false);
        if (this.selectionMode == 1) {
            newButton.setEnabled(false);
        }
        this.layoutSelect = findViewById(R.id.fdLinearLayoutSelect);
        this.layoutCreate = findViewById(R.id.fdLinearLayoutCreate);
        this.layoutCreate.setVisibility(View.GONE);
        Button cancelButton = findViewById(R.id.fdButtonCancel);
        // android.view.View.OnClickListener
        cancelButton.setOnClickListener(v -> {
            FileDialog.this.setSelectVisible(v);
            if (FileDialog.this.canSelectDir) {
                FileDialog.this.selectButton.setEnabled(true);
            }
        });
        Button createButton = findViewById(R.id.fdButtonCreate);
        // android.view.View.OnClickListener
        createButton.setOnClickListener(v -> {
            if (FileDialog.this.mFileName.getText().length() > 0) {
                if (!FileDialog.this.showFoldersOnly || !FileDialog.this.canSelectDir) {
                    FileDialog.this.getIntent().putExtra(FileDialog.RESULT_PATH, FileDialog.this.currentPath + "/" + FileDialog.this.mFileName.getText());
                    FileDialog.this.setResult(-1, FileDialog.this.getIntent());
                    FileDialog.this.finish();
                    return;
                }
                String newCurrePath = FileDialog.this.currentPath + "/" + FileDialog.this.mFileName.getText();
                boolean directoryCreated = false;
                try {
                    File newFolder = new File(newCurrePath);
                    directoryCreated = newFolder.mkdir();
                } catch (Exception ignored) {
                }
                if (directoryCreated) {
                    FileDialog.this.getIntent().putExtra(FileDialog.RESULT_PATH, newCurrePath);
                    FileDialog.this.setResult(-1, FileDialog.this.getIntent());
                    FileDialog.this.finish();
                    return;
                }
                // android.content.DialogInterface.OnClickListener
                new AlertDialog.Builder(FileDialog.this).setIcon(R.mipmap.icon).setTitle("[" + FileDialog.this.mFileName.getText() + "] " + FileDialog.this.getText(R.string.cant_create_folder)).setPositiveButton(ExternallyRolledFileAppender.OK, (dialog, which) -> {
                }).show();
            }
        });
        String startPath = getIntent().getStringExtra(START_PATH);
        if (startPath == null) {
            startPath = "/";
        }
        if (this.canSelectDir) {
            this.selectedFile = new File(startPath);
            this.selectButton.setEnabled(true);
        }
        getDir(startPath);
    }

    private void getDir(String dirPath) {
        boolean useAutoSelection = dirPath.length() < this.currentPath.length();
        Integer position = this.lastPositions.get(this.parentPath);
        getDirImpl(dirPath);
        if (position != null && useAutoSelection) {
            getListView().setSelection(position);
        }
    }

    private void getDirImpl(String dirPath) {
        this.currentPath = dirPath;
        List<String> item = new ArrayList<>();
        this.path = new ArrayList<>();
        this.mList = new ArrayList<>();
        File f = new File(this.currentPath);
        File[] files = f.listFiles();
        if (files == null) {
            this.currentPath = "/";
            f = new File(this.currentPath);
            files = f.listFiles();
        }
        this.myPath.setText(getText(R.string.location) + ": " + this.currentPath);
        if (!this.currentPath.equals("/")) {
            item.add("/");
            addItem("/", R.mipmap.folder);
            this.path.add("/");
            item.add("../");
            addItem("../", R.mipmap.folder);
            this.path.add(f.getParent());
            this.parentPath = f.getParent();
        }
        TreeMap<String, String> dirsMap = new TreeMap<>();
        TreeMap<String, String> dirsPathMap = new TreeMap<>();
        TreeMap<String, String> filesMap = new TreeMap<>();
        TreeMap<String, String> filesPathMap = new TreeMap<>();
        for (File file : files) {
            if (file.isDirectory()) {
                String dirName = file.getName();
                dirsMap.put(dirName, dirName);
                dirsPathMap.put(dirName, file.getPath());
            } else if (!this.showFoldersOnly) {
                String fileName = file.getName();
                String fileNameLwr = fileName.toLowerCase();
                if (this.formatFilter != null) {
                    boolean contains = false;
                    int i = 0;
                    while (true) {
                        if (i >= this.formatFilter.length) {
                            break;
                        }
                        String formatLwr = this.formatFilter[i].toLowerCase();
                        if (!fileNameLwr.endsWith(formatLwr)) {
                            i++;
                        } else {
                            contains = true;
                            break;
                        }
                    }
                    if (contains) {
                        filesMap.put(fileName, fileName);
                        filesPathMap.put(fileName, file.getPath());
                    }
                } else {
                    filesMap.put(fileName, fileName);
                    filesPathMap.put(fileName, file.getPath());
                }
            }
        }
        item.addAll(dirsMap.tailMap("").values());
        item.addAll(filesMap.tailMap("").values());
        this.path.addAll(dirsPathMap.tailMap("").values());
        this.path.addAll(filesPathMap.tailMap("").values());
        SimpleAdapter fileList = new SimpleAdapter(this, this.mList, R.layout.file_dialog_row, new String[]{ITEM_KEY, ITEM_IMAGE}, new int[]{R.id.fdrowtext, R.id.fdrowimage});
        for (String dir : dirsMap.tailMap("").values()) {
            addItem(dir, R.mipmap.folder);
        }
        for (String s : filesMap.tailMap("").values()) {
            addItem(s, R.drawable.file);
        }
        fileList.notifyDataSetChanged();
        setListAdapter(fileList);
    }

    private void addItem(String fileName, int imageId) {
        HashMap<String, Object> item = new HashMap<>();
        item.put(ITEM_KEY, fileName);
        item.put(ITEM_IMAGE, imageId);
        this.mList.add(item);
    }

    @Override // android.app.ListActivity
    protected void onListItemClick(ListView l, View v, int position, long id) {
        File file = new File(this.path.get(position));
        setSelectVisible(v);
        if (file.isDirectory()) {
            this.selectButton.setEnabled(false);
            if (file.canRead()) {
                this.lastPositions.put(this.currentPath, position);
                getDir(this.path.get(position));
                if (this.canSelectDir) {
                    this.selectedFile = file;
                    v.setSelected(true);
                    this.selectButton.setEnabled(true);
                    return;
                }
                return;
            }
            // android.content.DialogInterface.OnClickListener
            new AlertDialog.Builder(this).setIcon(R.mipmap.icon).setTitle("[" + file.getName() + "] " + getText(R.string.cant_read_folder)).setPositiveButton(ExternallyRolledFileAppender.OK, (dialog, which) -> {
            }).show();
            return;
        }
        this.selectedFile = file;
        v.setSelected(true);
        this.selectButton.setEnabled(true);
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            this.selectButton.setEnabled(false);
            if (this.layoutCreate.getVisibility() == View.VISIBLE) {
                this.layoutCreate.setVisibility(View.GONE);
                this.layoutSelect.setVisibility(View.VISIBLE);
                this.selectButton.setEnabled(this.canSelectDir);
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setCreateVisible(View v) {
        this.layoutCreate.setVisibility(View.VISIBLE);
        this.layoutSelect.setVisibility(View.GONE);
        this.inputManager.showSoftInputFromInputMethod(this.mFileName.getWindowToken(), 2);
        this.selectButton.setEnabled(false);
    }

    private void setSelectVisible(View v) {
        this.layoutCreate.setVisibility(View.GONE);
        this.layoutSelect.setVisibility(View.VISIBLE);
        this.inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        this.selectButton.setEnabled(false);
    }
}
