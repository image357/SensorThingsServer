/*
 * Copyright (C) 2017 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.persistence;

import de.fraunhofer.iosb.ilt.sta.model.core.Id;
import de.fraunhofer.iosb.ilt.sta.model.core.IdLong;
import de.fraunhofer.iosb.ilt.sta.model.core.IdString;

/**
 *
 * @author scf
 */
public interface IdManager {

    public Class<? extends Id> getIdClass();

    public Id parseId(String input);

    public static final IdManager ID_MANAGER_LONG = new IdManager() {
        @Override
        public Class<? extends Id> getIdClass() {
            return IdLong.class;
        }

        @Override
        public Id parseId(String input) {
            return new IdLong(Long.parseLong(input));
        }
    };

    public static final IdManager ID_MANAGER_STRING = new IdManager() {
        @Override
        public Class<? extends Id> getIdClass() {
            return IdString.class;
        }

        @Override
        public Id parseId(String input) {
            if (input.startsWith("'")) {
                String idString = input.substring(1, input.length() - 1);
                idString = idString.replaceAll("''", "'");
                return new IdString(idString);
            }
            return new IdString(input);
        }
    };
;

}
