package seedu.addressbook.data.person;

import seedu.addressbook.data.exception.IllegalValueException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Represents an PostalCode's postal code in the PostalCode book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPostalCode(String)}
 */
public class PostalCode {

    public static final String EXAMPLE = "119077";
    public static final String MESSAGE_POSTAL_CODE_CONSTRAINTS = "Postal codes should be in a 6-digit format";
    public static final String POSTAL_CODE_VALIDATION_REGEX = "\\d{6}";
    public static final int NUM_CHARS_AFTER_ADDRESS_SUBSTRING = 10;
    public static final int NUM_CHARS_BEFORE_POSTAL_SUBSTRING = 3;
    public static final String MESSAGE_ERROR_ADDRESS_NOT_FOUND = "Address not found.";
    public static final String ONEMAP_SEARCH_URL_FIRST_HALF = "https://developers.onemap.sg/commonapi/search?searchVal=";
    public static final String ONEMAP_SEARCH_URL_SECOND_HALF = "&returnGeom=N&getAddrDetails=Y";

    public final String value;
    private boolean isPrivate;

    /**
     * Validates given PostalCode.
     *
     * @throws IllegalValueException if given PostalCode string is invalid.
     */
    public PostalCode(String postalCode, boolean isPrivate) throws IllegalValueException {
        String trimmedPostalCode = postalCode.trim();
        this.isPrivate = isPrivate;
        if (!isValidPostalCode(trimmedPostalCode)) {
            throw new IllegalValueException(MESSAGE_POSTAL_CODE_CONSTRAINTS);
        }
        value = trimmedPostalCode;
    }

    /**
     * Returns true if a given string is a valid person PostalCode.
     */
    public static boolean isValidPostalCode(String test) {
        return test.matches(POSTAL_CODE_VALIDATION_REGEX);
    }


    /**
     * Retrieves address details from OneMap online database using 6-digit postal code as key.
     */
    public String retrieveMatchingAddress() throws IOException {
        try {
            String lineFromURL = getOutputFromOnemapURL();
            String address = getAddressFromUrlOutput(lineFromURL);
            if (address != null) return address;
        }
        catch(IOException e) {
            return MESSAGE_ERROR_ADDRESS_NOT_FOUND;
        }
        return MESSAGE_ERROR_ADDRESS_NOT_FOUND;
    }

    /**
     * Obtains output from OneMap URL.
     */
    private String getOutputFromOnemapURL() throws IOException {
        InputStream is = getOnemapInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        return br.readLine();
    }

    /**
     * Returns InputStream for given search string entered into OneMap through the HTML URL.
     */
    private InputStream getOnemapInputStream() throws IOException {
        String urlString = ONEMAP_SEARCH_URL_FIRST_HALF + this.value + ONEMAP_SEARCH_URL_SECOND_HALF;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    /**
     * Parses output from OneMap to obtain address information and returns the address.
     */
    private String getAddressFromUrlOutput(String lineFromURL) {
        int startIdx = 0, endIdx = 0;
        if (lineFromURL.contains("ADDRESS")) {
            startIdx = lineFromURL.indexOf("ADDRESS") + NUM_CHARS_AFTER_ADDRESS_SUBSTRING;
            endIdx = lineFromURL.indexOf("POSTAL") - NUM_CHARS_BEFORE_POSTAL_SUBSTRING;
            String address = lineFromURL.substring(startIdx, endIdx);
            return address;
        }
        return null;
    }


    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof PostalCode // instanceof handles nulls
                && this.value.equals(((PostalCode) other).value)); // state check
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
