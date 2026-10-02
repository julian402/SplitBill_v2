package ue.edu.co.splitbill.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.NumberFormat;
import java.util.Locale;

import ue.edu.co.splitbill.R;

// Ayudas de presentacion que usan varias pantallas: pesos con puntos y avatares con iniciales
public final class Format {

    private static final int[] AVATAR_BACKGROUNDS = {
            R.color.colorAvatar1, R.color.colorAvatar2, R.color.colorAvatar3, R.color.colorAvatar4, R.color.colorAvatar5
    };
    private static final int[] AVATAR_TEXTS = {
            R.color.colorOnAvatar1, R.color.colorOnAvatar2, R.color.colorOnAvatar3, R.color.colorOnAvatar4, R.color.colorOnAvatar5
    };

    private Format() {
        //impide crear objetos de esta clase
    }

    // 120000 -> "$ 120.000". El espacio es "no separable" para que el $ nunca quede solo al final de una linea
    public static String money(long amount) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CO"));
        return "$ " + numberFormat.format(amount);
    }

    // Circulo con la inicial del nombre; el color depende del nombre para que siempre sea el mismo
    public static void avatar(Context context, TextView tvAvatar, String name) {
        String text = name == null || name.trim().isEmpty() ? "?" : name.trim().substring(0, 1).toUpperCase(Locale.ROOT);
        int index = Math.abs((name == null ? 0 : name.hashCode()) % AVATAR_BACKGROUNDS.length);
        tvAvatar.setText(text);
        tvAvatar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(context, AVATAR_BACKGROUNDS[index])));
        tvAvatar.setTextColor(ContextCompat.getColor(context, AVATAR_TEXTS[index]));
    }
}
