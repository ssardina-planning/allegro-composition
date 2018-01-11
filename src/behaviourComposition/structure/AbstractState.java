package behaviourComposition.structure;

public abstract class AbstractState implements IState {
	public static enum StateSort {BOTH, FINAL, NONE, START}
    protected String name;
    protected StateSort sort;
    public AbstractState(String name) {
        this(name, StateSort.NONE);
    }
    
    public AbstractState(String name, StateSort sort) {
        if (name == null) {
            throw new NullPointerException("Name must not be null!");
        }
        if (sort == null) {
            sort = StateSort.NONE;
        }
        this.name = name;
        this.sort = sort;
    }
    public String getName() {
        return this.name;
    }
    
    public StateSort getSort() {
        return this.sort;
    }
    public abstract boolean isFinalState();
    public abstract boolean isStartState();
    public abstract AbstractState deepCopy();
    public void setFinal(boolean fin) {
        if (fin) {
            switch (this.sort) {
                case NONE:
                    this.sort = StateSort.FINAL;
                    break;
                case START:
                    this.sort = StateSort.BOTH;
                    break;
            }
        } else {
            switch (this.sort) {
                case FINAL:
                    this.sort = StateSort.NONE;
                    break;
                case BOTH:
                    this.sort = StateSort.START;
                    break;
            }
        }
    }
    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("Name must not be null!");
        }
        this.name = name;
    }
    
    public void setSort(StateSort sort) {
        if (sort == null) {
            sort = StateSort.NONE;
        }
        this.sort = sort;
    }
    
    public void setStart(boolean start) {
        if (start) {
            switch (this.sort) {
                case NONE:
                    this.sort = StateSort.START;
                    break;
                case FINAL:
                    this.sort = StateSort.BOTH;
                    break;
            }
        } else {
            switch (this.sort) {
                case START:
                    this.sort = StateSort.NONE;
                    break;
                case BOTH:
                    this.sort = StateSort.FINAL;
                    break;
            }
        }
    }
    
    /**
     * 
     */
    public void toggleFinal() {
        if (this.isFinalState()) {
            this.setFinal(false);
        } else {
            this.setFinal(true);
        }
    }

    public void toggleStart() {
        if (this.isStartState()) {
            this.setStart(false);
        } else {
            this.setStart(true);
        }
    }

}
