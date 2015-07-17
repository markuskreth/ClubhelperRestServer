package de.kreth.clubhelperbackend.string;

import java.util.List;

public class String {

	public static java.lang.String join(java.lang.String string,
			List<? extends CharSequence> elements) {
		CharSequence[] arr = new CharSequence[elements.size()];
		elements.toArray(arr);
		return join(string, arr);
	}

	public static java.lang.String join(java.lang.String string,
			CharSequence[] arr) {
		StringBuilder bld;
		if(arr.length>0)
			bld = new StringBuilder(arr[0]);
		else
			bld = new StringBuilder();
		for(int i=1; i<arr.length;i++)
			bld.append(string).append(arr[i]);
		return bld.toString();
	}

}
