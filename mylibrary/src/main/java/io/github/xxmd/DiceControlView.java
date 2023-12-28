package io.github.xxmd;

import android.content.Context;
import android.content.res.TypedArray;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DiceControlView extends FrameLayout {
    private int diceCount = 1;
    private ViewGroup curContainer;
    private ViewGroup oneDiceLayout;
    private ViewGroup twoDiceLayout;
    private ViewGroup threeDiceLayout;
    private Integer[] diceIcons = {R.drawable.dice_1, R.drawable.dice_2, R.drawable.dice_3, R.drawable.dice_4, R.drawable.dice_5, R.drawable.dice_6 };
    private TypedArray typedArray;
    private @io.reactivex.rxjava3.annotations.NonNull Disposable interval;
    private MediaPlayer mediaPlayer;
    private int[] result;

    public int getDiceCount() {
        return diceCount;
    }

    public void setDiceCount(int diceCount) {
        this.diceCount = diceCount;
        switchLayoutByCount();
    }

    public void setDiceValue(int ...values) {
        if (values.length != diceCount) {
            throw new RuntimeException("values length not equals dice count");
        }
        for (int i = 0; i < diceCount; i++) {
            ImageView imageView = (ImageView) curContainer.getChildAt(i);
            imageView.setImageResource(diceIcons[values[i] - 1]);
        }
    }

    private int[] getRandomSequence() {
        int[] sequence = new int[diceCount];
        for (int i = 0; i < diceCount; i++) {
            sequence[i] = (int) (Math.random() * 6 + 1);
        }
        return sequence;
    }

    public void randomRoll(long rollDuration, int rollTimes, Consumer<int[]> onRollFinished) {
        stopRoll();
        stopSoundEffect();

        int period = (int) (rollDuration / rollTimes);
        playSoundEffect();
        interval = Observable.interval(period, TimeUnit.MILLISECONDS)
                .takeUntil(times -> times > rollTimes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> shakeDices(rollDuration, rollTimes))
                .subscribe(times -> {
                    result = getRandomSequence();
                    setDiceValue(result);
                }, throwable -> {
                    System.out.println(throwable);
                }, () -> {
                    stopSoundEffect();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        onRollFinished.accept(result);
                    }
                });
    }

    public void randomRoll(Consumer<int[]> onRollFinished) {
        randomRoll(2000, 20, onRollFinished);
    }

    private void shakeDices(long rollDuration, int rollTimes) {
        for (int i = 0; i < diceCount; i++) {
            ImageView imageView = (ImageView) curContainer.getChildAt(i);
            Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
            shake.setDuration(rollDuration);
            imageView.startAnimation(shake);
        }
    }

    private void playSoundEffect() {
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.dice_roll);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }

    private void stopSoundEffect() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void stopRoll() {
        if (interval != null) {
            interval.dispose();
        }
    }

    private void switchLayoutByCount() {
        oneDiceLayout.setVisibility(GONE);
        twoDiceLayout.setVisibility(GONE);
        threeDiceLayout.setVisibility(GONE);

        switch (diceCount) {
            case 1:
                oneDiceLayout.setVisibility(VISIBLE);
                curContainer = oneDiceLayout;
                break;
            case 2:
                twoDiceLayout.setVisibility(VISIBLE);
                curContainer = twoDiceLayout;
                break;
            case 3:
                threeDiceLayout.setVisibility(VISIBLE);
                curContainer = threeDiceLayout;
                break;
        }
    }

    public DiceControlView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        initView();
        if (typedArray != null) {
            initByTypedArray();
        }
    }

    private void initByTypedArray() {
        int diceCount = typedArray.getInt(R.styleable.DiceControlView_diceCount, 1);
        setDiceCount(diceCount);
    }

    private void initView() {
        View rootView = inflate(getContext(), R.layout.dice_control_view, this);
        oneDiceLayout = rootView.findViewById(R.id.one_dice_layout);
        twoDiceLayout = rootView.findViewById(R.id.two_dice_layout);
        threeDiceLayout = rootView.findViewById(R.id.three_dice_layout);
    }

    public DiceControlView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.DiceControlView);
        init();
    }
}
