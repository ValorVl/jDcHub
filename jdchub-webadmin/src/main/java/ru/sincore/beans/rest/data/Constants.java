package ru.sincore.beans.rest.data;

/**
 * JDcHub REST Constants
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 08.02.12
 *         Time: 11:17
 */
public class Constants
{
    /**
     * User status constants
     */
    public class UserStatus
    {
        public static final int UNREGISTERED_USER = 0;
        public static final int REGISTERED_USER = 1;
        public static final int SUPER_USER = 2;
    }


    /**
     * Errors constants
     */
    public enum Error
    {
        NO_ERROR              (0, "No error"),
        AUTH_ERROR            (1, "Auth error"),
        NOT_IMPLEMENTED_ERROR (2, "Method not implemented yet"),
        NO_ACCESS             (3, "You have no access to this data"),
        OPERATION_FAIL        (4, "Operation fail"),
        USER_NOT_FOUND        (5, "User not found");


        private int errorCode = -1;
        private String errorText = "";
        Error(int errorCode, String errorText)
        {
            this.errorCode = errorCode;
            this.errorText = errorText;
        }


        /**
         * Get error code for given constant
         * @return error code greater or equal zero for valid constants or -1 for invalid
         */
        public int errorCode()
        {
            return this.errorCode;
        }


        /**
         * Get error description for giver constant
         * @return error string
         */
        public String errorText()
        {
            return this.errorText;
        }


        /**
         * Get error constant by it error code
         *
         * @param errorCode error code
         * @return error constant or <code>null</code> if invalid
         */
        public Error evalErrorCode(int errorCode)
        {
            for (Error error : Error.values())
            {
                if (error.errorCode() == errorCode)
                {
                    return error;
                }
            }

            return null;
        }
    }

}
