package jp.co.casio.caios.demo.dbaccessdemo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

@SuppressWarnings("ALL")
public class DataBaseAccessDemoActivity extends Activity implements View.OnClickListener {
	private Handler handler = new Handler();
	private final static String PROVIDER = "jp.co.casio.caios.framework.database";
	private static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
	private static final int FP = ViewGroup.LayoutParams.FILL_PARENT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Resources resources = getResources();

		// データベース名にフォーカスを移動
		EditText et;
//		et = (EditText) findViewById(R.id.editText_db);
//		et.requestFocus();

		// 検索時間 クリア
		TextView textView_time;
		textView_time = (TextView) findViewById(R.id.textView_time);
		textView_time.setText("?" + resources.getString(R.string.add_time));

		// 検索件数 クリア
		TextView textView_cnt;
		textView_cnt = (TextView) findViewById(R.id.textView_count);
		textView_cnt.setText("?" + resources.getString(R.string.add_count));

		// 各ボタンにリスナーを設定
		Button button;
		button = (Button) findViewById(R.id.button_finish);
		button.setOnClickListener(this);

		button = (Button) findViewById(R.id.button_execution);
		button.setOnClickListener(this);
	}

//	@Override
//	public void onWindowFocusChanged(boolean hasFocus) {
//		// 起動直後にソフトキーボードを表示する
//		super.onWindowFocusChanged(hasFocus);
//		if (hasFocus) {
//			LinearLayout linearLayout_result;
//			linearLayout_result = (LinearLayout) findViewById(R.id.LinearLayout_result);
//			if (linearLayout_result.getChildCount() == 0) {
//				Message m = Message.obtain(handler, new Runnable() {
//					@Override
//					public void run() {
//						// ソフトキーボード表示
//						EditText et;
//						et = (EditText) findViewById(R.id.editText_db);
//						if (et.isFocused()) {
//							InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//							inputMethodManager.showSoftInput(et, 0);
//						}
//					}
//				});
//				handler.sendMessage(m);
//			}
//		}
//	}

	@Override
	public void onClick(View v) {
		// ソフトキーボード非表示
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean r = inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

		switch (v.getId()) {
		case R.id.button_finish:
			if (!r) {
				// ソフトキーボードが非表示状態なら、デモアプリを終了
				finish();
			}
			break;
		case R.id.button_execution:
			// DB検索


			new queryTask(this).execute();
			break;
		}
	}

	private static class queryTask extends AsyncTask<Void, Integer, TableLayout> {
		private Activity mActivity;
		private ProgressDialog mProgressDialog = null;

		public queryTask(Activity activity) {
			mActivity = activity;
		}

		@Override
		protected void onPreExecute() {
			Resources resources = mActivity.getResources();

			// 検索時間 クリア
			TextView textView_time;
			textView_time = (TextView) mActivity.findViewById(R.id.textView_time);
			textView_time.setText("?" + resources.getString(R.string.add_time));

			// 検索件数 クリア
			TextView textView_cnt;
			textView_cnt = (TextView) mActivity.findViewById(R.id.textView_count);
			textView_cnt.setText("?" + resources.getString(R.string.add_count));

			// 検索結果 クリア
			LinearLayout linearLayout_result;
			linearLayout_result = (LinearLayout) mActivity.findViewById(R.id.LinearLayout_result);
			linearLayout_result.removeAllViews();
		}

		@Override
		protected TableLayout doInBackground(Void... arg0) {
			String db = "SUMMARYBACK.DB";
			String table = "CSS034";
			String projection[] = {"ITEMNAME","QTY","AMT"};
			String selection = null; // BIZDATE='20151211" บังคับใส่แบบนี้
			String selectionArgs[] = null;
			String sortOrder = null;

			// 検索結果格納用テーブル
			TableLayout tableLayout_result;
			tableLayout_result = new TableLayout(mActivity);

			// データベース名 取得
			EditText et;
//			et = (EditText) mActivity.findViewById(R.id.editText_db);

//			db = et.getText().toString();
//
//			// テーブル名 取得
//			et = (EditText) mActivity.findViewById(R.id.editText_table);

//			table = et.getText().toString();
//
//			// データベース名とテーブル名は必ず必要
//			if ((et.length() == 0) || (table.length() == 0)) {
//				return tableLayout_result;
//			}

			// フィールド名 取得
//			et = (EditText) mActivity.findViewById(R.id.editText_projection);
//			if (et.getText().length() > 0) {
//				projection = et.getText().toString().split(" ");
//				if (projection.length == 0) {
//					projection = null;
//				}
//			}

			// 検索条件 取得
			et = (EditText) mActivity.findViewById(R.id.editText_selection);
			selection =  "BIZDATE = '" + et.getText().toString() + "'";
			if (selection.length() == 0) {
				selection = null;
			}

			// URI作成
			Uri.Builder builder = new Uri.Builder();
			builder.scheme("content");
			builder.authority(PROVIDER);
			builder.appendPath("SUMMARYBACK");
			builder.appendPath("CSS034");
			Uri uri = builder.build();

			// 検索してカーソルを取得する
			long time = 0;
			Cursor cursor = null;
			try {
				// 検索開始時間
				time = System.currentTimeMillis();

				//-------------------------------------------Start
				// コンテンツプロバイダーからデータを取得する
				cursor = mActivity.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
				//-------------------------------------------End


				// 検索終了時間
				time = System.currentTimeMillis() - time;
			}
			catch (Exception e) {
				// 検索失敗
			}
			if (cursor == null) {
				return null;
			}

			// 検索結果を表示領域に設定
			int countMax = cursor.getCount();
			publishProgress(0, countMax, (int) time);
			if (countMax > 0) {
				int columnCount = cursor.getColumnCount();
				// 先頭の５列までを表示する
				if (columnCount > 5) {
					columnCount = 5;
				}
				tableLayout_result.setColumnStretchable(columnCount - 1, true);

				String[] columnName = new String[columnCount];
				int[] columnIndex = new int[columnCount];
				TableRow tableRow = new TableRow(mActivity);
				for (int i = 0; i < columnCount; i++) {
					columnName[i] = cursor.getColumnName(i);
					columnIndex[i] = cursor.getColumnIndex(columnName[i]);
					TextView str = new TextView(mActivity);
					str.setText(columnName[i] + " ");
					str.setTextSize(24.0f);
					str.setTextColor(Color.WHITE);
					str.setBackgroundColor(Color.RED);
					tableRow.addView(str);
				}
				tableLayout_result.addView(tableRow, new TableLayout.LayoutParams(FP, WC));

				int count = 0;
				if (cursor.moveToFirst()) {
					do {
						count++;
						publishProgress(count, countMax);
						tableRow = new TableRow(mActivity);
						for (int i = 0; i < columnCount; i++) {
							String s = cursor.getString(columnIndex[i]);

							TextView str = new TextView(mActivity);
							str.setText(s + " ");
							str.setTextSize(24.0f);
							tableRow.addView(str);
						}
						tableLayout_result.addView(tableRow, new TableLayout.LayoutParams(FP, WC));
					} while (cursor.moveToNext());
				}
			}

			cursor.close();

			return tableLayout_result;
		}

		@Override
		protected void onPostExecute(TableLayout result) {
			if (result != null) {
				// 検索結果 表示
				LinearLayout linearLayout_result;
				linearLayout_result = (LinearLayout) mActivity.findViewById(R.id.LinearLayout_result);
				linearLayout_result.addView(result);
			}

			// プログレスバー 非表示
			if (mProgressDialog != null) {
				if (mProgressDialog.isShowing()) {
					mProgressDialog.dismiss();
				}
			}

			mActivity = null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			if (mProgressDialog == null) {
				Resources resources = mActivity.getResources();

				// 検索時間 表示
				TextView textView_time;
				textView_time = (TextView) mActivity.findViewById(R.id.textView_time);
				textView_time.setText(String.valueOf(values[2]) + resources.getString(R.string.add_time));

				// 検索件数 表示
				TextView textView_cnt;
				textView_cnt = (TextView) mActivity.findViewById(R.id.textView_count);
				textView_cnt.setText(String.valueOf(values[1]) + resources.getString(R.string.add_count));

				// プログレスバー 表示
				String message;
				message = resources.getString(R.string.message1);
				message += " " + String.valueOf(values[2]) + resources.getString(R.string.add_time);
				message += " " + String.valueOf(values[1]) + resources.getString(R.string.add_count) + "\n";
				message += resources.getString(R.string.message2);
				mProgressDialog = new ProgressDialog(mActivity);
				mProgressDialog.setMessage(message);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				mProgressDialog.setProgress(0);
				mProgressDialog.setMax(values[1]);
				mProgressDialog.setCancelable(false);
				mProgressDialog.show();
			}
			else {
				// プログレスバー 更新
				mProgressDialog.setProgress(values[0]);
			}
		}
	}

}