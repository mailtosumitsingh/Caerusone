package org.ptg.util;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class DiagnosticException extends RuntimeException{

    private static final long serialVersionUID = 5589635876875819926L;
	private Diagnostic<? extends JavaFileObject> diagnostic;

    public DiagnosticException(String message) {
        super(message);
    }

    public DiagnosticException(Throwable cause) {
        super(cause);
    }

    public DiagnosticException(Diagnostic<? extends JavaFileObject> diagnostic) {
        super(diagnostic.toString());
        this.diagnostic  = diagnostic;
    }

	public Diagnostic<? extends JavaFileObject> getDiagnostic() {
		return diagnostic;
	}

	public void setDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
		this.diagnostic = diagnostic;
	}
    
}