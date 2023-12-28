package io.github.xxmd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Arrays;

import io.github.xxmd.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindEvent();
    }

    private void bindEvent() {
        View root = binding.getRoot();
        root.setOnClickListener(v -> {
            binding.diceControlView.randomRoll(2000, 20, ints -> {
                Toast.makeText(MainActivity.this, Arrays.toString(ints), Toast.LENGTH_SHORT).show();
            });
        });

        binding.btn1.setOnClickListener(v -> binding.diceControlView.setDiceCount(1));
        binding.btn2.setOnClickListener(v -> binding.diceControlView.setDiceCount(2));
        binding.btn3.setOnClickListener(v -> binding.diceControlView.setDiceCount(3));
    }
}