package de.kreth.clubhelperbackend.pojo;

import java.util.Locale;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Entity mapped to table "CONTACT".
 */
public class Contact extends AbstractData {

	private static final long serialVersionUID = 9210368716677650420L;
	private String type;
	private String value;
	private long personId;

	public Contact() {
	}

	public Contact(Long id, String type, String value, long personId) {
		super(id);
		this.type = type;
		this.value = value;
		this.personId = personId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	@Override
	public String toString() {
		if (type.toLowerCase(Locale.getDefault()).startsWith("tele")
				|| type.toLowerCase(Locale.getDefault()).matches("mobile")) {

			PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

			try {
				Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(value,
						Locale.getDefault().getCountry());
				if (phoneUtil.isValidNumber(phoneNumber))
					return type + ": " + phoneUtil.format(phoneNumber,
							PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return type + ": " + value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (personId ^ (personId >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contact other = (Contact) obj;
		if (personId != other.personId)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
