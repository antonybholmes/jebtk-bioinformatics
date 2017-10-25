package org.jebtk.bioinformatics.genomic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mutation {
	private static final Pattern MUTATION_REGEX =
			Pattern.compile("([a-zA-Z]+)(\\d+)([a-zA-Z]+)");
	
	private final String mFrom;
	private final String mTo;
	private final MutationType mType;
	private final int mLocation;

	public Mutation(int location, String from, String to) {
		mLocation = location;
		mFrom = from;
		mTo = to;
		
		if (from.length() > to.length()) {
			mType = MutationType.DELETION;
		} else if (from.length() < to.length()) {
			mType = MutationType.INSERTION;
		} else {
			mType = MutationType.CHANGE;
		}
	}
	
	public int getLocation() {
		return mLocation;
	}
	
	public String getFrom() {
		return mFrom;
	}
	
	public String getTo() {
		return mTo;
	}
	
	public MutationType getType() {
		return mType;
	}
	
	public static Mutation parse(String s) {
		Matcher matcher = MUTATION_REGEX.matcher(s);

		if (matcher.find()) {
			String from = matcher.group(1);
			int location = Integer.parseInt(matcher.group(2));
			String to = matcher.group(2);
			
			Mutation mutation = new Mutation(location, from, to);
			
			return mutation;
		} else {
			return null;
		}
	}
}
