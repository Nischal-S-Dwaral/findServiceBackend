package org.msc.web.dev.exceptions;

import java.util.function.Supplier;

/**
 *
 * BadRequest Exception Handler
 *
 * @author faiz.syed
 *
 */

public class BadRequest extends CustomException implements Supplier {

    private static final long serialVersionUID = 788618025995649201L;

    public BadRequest(String description) {
        super(description);
    }

    @Override
    public Object get() {
        return null;
    }
}
