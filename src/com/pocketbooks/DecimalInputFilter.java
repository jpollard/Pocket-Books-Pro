package com.pocketbooks;

import java.text.DecimalFormatSymbols;

import android.text.InputFilter;
import android.text.Spanned;

public class DecimalInputFilter implements InputFilter {

	int decimalPlace;
	DecimalFormatSymbols dec;
	char decimalSymbol;
	
	public DecimalInputFilter(int decimalPlace){
		this.decimalPlace = decimalPlace;
		dec = new DecimalFormatSymbols();
		decimalSymbol = dec.getDecimalSeparator();
	}
	
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int destStart, int destEnd) {
		// TODO Auto-generated method stub
		int decimalPosition = -1;
		int len = dest.length();
		
		for(int i = 0; i < len; i++){
			if(dest.charAt(i) == decimalSymbol){
				decimalPosition = i;
				break;
			}
		}
		
		if(decimalPosition > 0){
			if(destEnd <= decimalPosition){
				return null;
			}
			if(len - decimalPosition > decimalPlace){
				return "";
			}
		}
		return null;

	}

}
