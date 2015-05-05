/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

/*
 *                        AT&T - PROPRIETARY
 *          THIS FILE CONTAINS PROPRIETARY INFORMATION OF
 *        AT&T AND IS NOT TO BE DISCLOSED OR USED EXCEPT IN
 *             ACCORDANCE WITH APPLICABLE AGREEMENTS.
 *
 *          Copyright (c) 2013 AT&T Knowledge Ventures
 *              Unpublished and Not for Publication
 *                     All Rights Reserved
 */
package com.att.research.xacmlatt.pdp.std.combiners;

import java.util.Iterator;
import java.util.List;

import com.att.research.xacml.api.Decision;
import com.att.research.xacml.api.Identifier;
import com.att.research.xacmlatt.pdp.eval.EvaluationContext;
import com.att.research.xacmlatt.pdp.eval.EvaluationException;
import com.att.research.xacmlatt.pdp.eval.EvaluationResult;
import com.att.research.xacmlatt.pdp.policy.CombinerParameter;
import com.att.research.xacmlatt.pdp.policy.CombiningElement;

/**
 * PermitOverrides extends {@link com.att.research.xacmlatt.pdp.std.combiners.CombiningAlgorithmBase} to
 * implement the XACML 3.0 Permit-overrides combining algorithm for policies and rules.
 *
 * @param <T> the java class of the object to be combined
 */
public class PermitOverrides<T extends com.att.research.xacmlatt.pdp.eval.Evaluatable> extends
    CombiningAlgorithmBase<T> {

    public PermitOverrides(Identifier identifierIn) {
        super(identifierIn);
    }

    @Override
    public EvaluationResult combine(EvaluationContext evaluationContext, List<CombiningElement<T>> elements,
                                    List<CombinerParameter> combinerParameters) throws EvaluationException {
        boolean atLeastOneDeny = false;

        EvaluationResult combinedResult = new EvaluationResult(Decision.DENY);

        EvaluationResult firstIndeterminateD = null;
        EvaluationResult firstIndeterminateP = null;
        EvaluationResult firstIndeterminateDP = null;

        Iterator<CombiningElement<T>> iterElements = elements.iterator();
        while (iterElements.hasNext()) {
            CombiningElement<T> combiningElement = iterElements.next();
            EvaluationResult evaluationResultElement = combiningElement.evaluate(evaluationContext);

            assert (evaluationResultElement != null);
            switch (evaluationResultElement.getDecision()) {
            case DENY:
                atLeastOneDeny = true;
                combinedResult.merge(evaluationResultElement);
                break;
            case INDETERMINATE:
            case INDETERMINATE_DENYPERMIT:
                if (firstIndeterminateDP == null) {
                    firstIndeterminateDP = evaluationResultElement;
                } else {
                    firstIndeterminateDP.merge(evaluationResultElement);
                }
                break;
            case INDETERMINATE_DENY:
                if (firstIndeterminateD == null) {
                    firstIndeterminateD = evaluationResultElement;
                } else {
                    firstIndeterminateD.merge(evaluationResultElement);
                }
                break;
            case INDETERMINATE_PERMIT:
                if (firstIndeterminateP == null) {
                    firstIndeterminateP = evaluationResultElement;
                } else {
                    firstIndeterminateP.merge(evaluationResultElement);
                }
                break;
            case NOTAPPLICABLE:
                break;
            case PERMIT:
                return evaluationResultElement;
            default:
                throw new EvaluationException("Illegal Decision: \""
                                              + evaluationResultElement.getDecision().toString());
            }
        }

        if (firstIndeterminateDP != null) {
            return firstIndeterminateDP;
        } else if (firstIndeterminateP != null && (firstIndeterminateD != null || atLeastOneDeny)) {
            return new EvaluationResult(Decision.INDETERMINATE_DENYPERMIT, firstIndeterminateD.getStatus());
        } else if (firstIndeterminateP != null) {
            return firstIndeterminateP;
        } else if (atLeastOneDeny) {
            return combinedResult;
        } else if (firstIndeterminateD != null) {
            return firstIndeterminateD;
        } else {
            return new EvaluationResult(Decision.NOTAPPLICABLE);
        }
    }

}
