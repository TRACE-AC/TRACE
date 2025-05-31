public double solve(final UnivariateRealFunction f, double min, double max, double initial)
         throws MaxIterationsExceededException, FunctionEvaluationException {
         return solve(f, min, max); // Modified to pass f to the solver routine, preventing the NullPointerException
     }
