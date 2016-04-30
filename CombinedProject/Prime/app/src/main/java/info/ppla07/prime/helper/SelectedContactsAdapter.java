package info.ppla07.prime.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.ppla07.prime.R;

public class SelectedContactsAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;



    public SelectedContactsAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.selected_contact_items, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
        listItemText.setText(list.get(position));

        //Handle buttons and add onClickListeners
        Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);

        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.d("Error", list.get(position));
                String[] contact = list.get(position).split("|");
                SharedPreferences sharedpreferences = context.getSharedPreferences("MyPreference", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                String[] names = sharedpreferences.getString("EmergencyContactsNames", "").split(";");
                String[] numbers = sharedpreferences.getString("EmergencyContactsNumbers", "").split(";");
                String newName = "";
                String newNumber = "";
                for(int i = 0; i < names.length; i++) {
                    if(i == position) {
                        continue;
                    }
                    else {
                        newName += names[i] + ";";
                        newNumber += numbers[i] + ";";
                    }
                }
                if(newName.equals(";")) {
                    newName = "";
                    newNumber = "";
                }
                editor.putString("EmergencyContactsNames", newName);
                editor.putString("EmergencyContactsNumbers", newNumber);
                editor.commit();

                CharSequence text = "Delete successful";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}