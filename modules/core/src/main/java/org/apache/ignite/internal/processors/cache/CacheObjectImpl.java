/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.cache;

import org.apache.ignite.*;
import org.jetbrains.annotations.*;

/**
 *
 */
public class CacheObjectImpl extends CacheObjectAdapter {
    /**
     *
     */
    public CacheObjectImpl() {
        // No-op.
    }

    /**
     * @param val Value.
     * @param valBytes Value bytes.
     */
    public CacheObjectImpl(Object val, byte[] valBytes) {
        assert val != null || valBytes != null;

        this.val = val;
        this.valBytes = valBytes;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Nullable @Override public <T> T value(CacheObjectContext ctx, boolean cpy) {
        cpy = cpy && needCopy(ctx);

        try {
            if (cpy) {
                return (T)ctx.processor().unmarshal(ctx, valBytes,
                    val == null ? ctx.kernalContext().config().getClassLoader() : val.getClass().getClassLoader());
            }

            if (val != null)
                return (T)val;

            assert valBytes != null;

            val = ctx.processor().unmarshal(ctx, valBytes, ctx.kernalContext().config().getClassLoader());
        }
        catch (IgniteCheckedException e) {
            throw new IgniteException("Failed to unmarshal object.", e);
        }

        return (T)val;
    }

    /** {@inheritDoc} */
    @Override public byte[] valueBytes(CacheObjectContext ctx) throws IgniteCheckedException {
        if (valBytes == null)
            valBytes = ctx.processor().marshal(ctx, val);

        return valBytes;
    }

    /** {@inheritDoc} */
    @Override public void prepareMarshal(CacheObjectContext ctx) throws IgniteCheckedException {
        assert val != null || valBytes != null;

        if (valBytes == null)
            valBytes = ctx.kernalContext().cacheObjects().marshal(ctx, val);
    }

    /** {@inheritDoc} */
    @Override public void finishUnmarshal(CacheObjectContext ctx, ClassLoader ldr) throws IgniteCheckedException {
        assert val != null || valBytes != null;

        if (val == null && ctx.unmarshalValues())
            val = ctx.processor().unmarshal(ctx, valBytes, ldr);
    }

    /** {@inheritDoc} */
    @Override public byte directType() {
        return 89;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        assert false;

        return super.hashCode();
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object obj) {
        assert false;

        return super.equals(obj);
    }

    /** {@inheritDoc} */
    @Override public CacheObject prepareForCache(CacheObjectContext ctx) {
        return this;
    }
}
