package org.ptg.util;



public class PropInfo<Type> extends AbstractHierStore<Type> implements IHierStore<Type>  {
	private String writeSig;
	private String readSig;


	public String getWriteSig() {
		return writeSig;
	}
	public void setWriteSig(String writeSig) {
		this.writeSig = writeSig;
	}
	public String getReadSig() {
		return readSig;
	}
	public void setReadSig(String readSig) {
		this.readSig = readSig;
	}
		@Override
	public String toString() {
		return "PropInfo [name=" + name + ", propClass=" + propClass + "]";
	}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((readSig == null) ? 0 : readSig.hashCode());
			result = prime * result + ((writeSig == null) ? 0 : writeSig.hashCode());
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
			PropInfo other = (PropInfo) obj;
			if (readSig == null) {
				if (other.readSig != null)
					return false;
			} else if (!readSig.equals(other.readSig))
				return false;
			if (writeSig == null) {
				if (other.writeSig != null)
					return false;
			} else if (!writeSig.equals(other.writeSig))
				return false;
			return true;
		}
		
	
}
