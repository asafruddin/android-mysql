package com.example.sem_i;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    ArrayList<String> organizer, title, tanggal, lokasi, idInfTrain, idTrainProv, url;
    RecyclerView recyclerView;
    TextView noEvent;
    String mail;
    Calendar calendar;
    SimpleDateFormat dateFormat;
    String date;
    Toolbar toolbar;
    private static final String TAG = "HomeFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.homeRecyler);
        noEvent = view.findViewById(R.id.noEvent);
        toolbar = view.findViewById(R.id.toolbar);
        organizer = new ArrayList<String>();
        title = new ArrayList<String>();
        tanggal = new ArrayList<String>();
        lokasi = new ArrayList<String>();
        idInfTrain = new ArrayList<String>();
        idTrainProv = new ArrayList<String>();
        url = new ArrayList<>();
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        date = dateFormat.format(calendar.getTime());
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("email", MODE_PRIVATE);
        mail = sharedPreferences.getString("mail", "");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        getInfTrainProv(mail);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getInfTrainProv(String email){
        AndroidNetworking.get(RegisterActivity.ip+"android/getInfoTraining.php")
                .addQueryParameter("email", email)
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, "onResponse" + response);
                        {
                            try {
                                for (int i = 0; i<response.length();i++){
                                    JSONObject data = response.getJSONObject(i);
                                    if (data.getString("tgl").compareTo(date)>0){
                                        organizer.add(data.getString("nama_trainprov"));
                                        title.add(data.getString("title"));
                                        tanggal.add(data.getString("tgl"));
                                        lokasi.add(data.getString("lokasi"));
                                        idInfTrain.add(data.getString("id"));
                                        idTrainProv.add(data.getString("idtrainprov"));
                                        url.add(data.getString("url"));
                                    }
                                }
                                if (organizer!=null && organizer.size()!=0){
                                    ListDataAdapter adapter = new ListDataAdapter(getActivity(), organizer, title, tanggal, lokasi, idInfTrain, idTrainProv, url);
                                    recyclerView.setAdapter(adapter);
                                    noEvent.setVisibility(View.GONE);
                                }else
                                    noEvent.setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(getContext(), "Error!!! "+ anError, Toast.LENGTH_SHORT);
                    }
                });
    }
}
