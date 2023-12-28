package io.github.xxmd;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
        LinearLayout root = binding.getRoot();
        root.setOnClickListener(v -> {
            binding.diceControlView.randomRoll(2000, 20, ints -> {
                Toast.makeText(MainActivity.this, Arrays.toString(ints), Toast.LENGTH_SHORT).show();
            });
        });
    }
}