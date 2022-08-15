
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*
 Licensed under gpl.
 Copyright (c) 2010 sumit singh
 http://www.gnu.org/licenses/gpl.html
 Use at your own risk
 Other licenses may apply please refer to individual source files.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
	int index();

	boolean searchable();
}
