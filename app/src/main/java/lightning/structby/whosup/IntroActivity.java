package lightning.structby.whosup;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: An images for slides
        addSlide(AppIntroFragment.newInstance("Welcome!", "Welcome to Who's Up?!", R.drawable.person, Color.parseColor("#79BF28")));
        addSlide(AppIntroFragment.newInstance("Bored?", "Now you can find fun stuff to do!", R.drawable.person, Color.parseColor("#0DA8F2")));
        addSlide(AppIntroFragment.newInstance("See what's happening nearby", "Using our super cool map view!", R.drawable.person, Color.parseColor("#4D6A78")));
        addSlide(AppIntroFragment.newInstance("Get Started", "What are you waiting for?", R.drawable.person, Color.parseColor("#F32622")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(false);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        this.finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}