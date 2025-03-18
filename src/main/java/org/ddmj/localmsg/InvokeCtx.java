package org.ddmj.localmsg;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class InvokeCtx {

    private String className;

    private String methodName;

    private String paramTypes;

    private String args;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final InvokeCtx invokeCtx;

        public Builder() {
            this.invokeCtx = new InvokeCtx();
        }

        public Builder className(String className) {
            this.invokeCtx.setClassName(className);
            return this;
        }

        public Builder methodName(String methodName) {
            this.invokeCtx.setMethodName(methodName);
            return this;
        }

        public Builder paramTypes(String paramTypes) {
            this.invokeCtx.setParamTypes(paramTypes);
            return this;
        }

        public Builder args(String args) {
            this.invokeCtx.setArgs(args);
            return this;
        }

        public InvokeCtx build() {
            return this.invokeCtx;
        }
    }
}
