package com.google.ar.sceneform.samples.ARFurniture;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentPerson extends Fragment {
    @Nullable
    Button button;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_person, container, false);

        //点击按钮实现跳转效果
        button = (Button)view.findViewById(R.id.btn_reset);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ResetPwdActivity.class);
                startActivity(i);
            }
        });
        return view;
    }
}
