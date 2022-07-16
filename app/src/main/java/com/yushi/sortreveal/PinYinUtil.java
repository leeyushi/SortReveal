package com.yushi.sortreveal;

import android.util.Log;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtil {
    public  static String chineseToPinYin(char value) {
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        if (value > 128) {
            try {
                String[] strings = PinyinHelper.toHanyuPinyinStringArray(value, outputFormat);
                return String.valueOf(strings[0].toCharArray()[0]);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void getPinYin(String value) {
        String simpleStr = value.replaceAll("\\-|\\s", "");
        char[] chars = simpleStr.toCharArray();
        HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
        outputFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        StringBuilder builder = new StringBuilder();
        for (char cv : chars) {
            if (cv > 128) {
                String[] strings = null;
                try {
                    strings = PinyinHelper.toHanyuPinyinStringArray(cv, outputFormat);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
                if (strings != null && strings.length > 0) {
                    builder.append(strings[0]);
                }
            } else {
                builder.append(cv);
            }
        }
        Log.e("", "strings==>" + builder.toString());
    }
}
