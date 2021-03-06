/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.report.model.generator;

import org.sigmah.server.domain.User;
import org.sigmah.shared.dto.pivot.model.ReportElement;
import org.sigmah.shared.util.DateRange;
import org.sigmah.shared.util.Filter;

/**
 * Generates the <code>Content</code> for a given <code>ReportElement</code>.
 *
 * @see org.sigmah.shared.report.model.ReportElement
 *
 * @param <T> The type of <code>ReportElement</code> accepted by the ContentGenerator implementor.
 * @author Alexander Bertram (akbertram@gmail.com)
 */
public interface ContentGenerator<T extends ReportElement> {


    /**
     * Retrieves data and computes and shapes this data into a presentation-ready format.
     * The generated result is stored in <code>element.content</code> upon completion.
     *
     * @param user The user for whom this content is generated. Effects such things as locale and
     *   access restrictions.
     * @param element The definition of the element for which to generate content
     * @param inheritedFilter Any filter inherited from an enclosing report or other container that should
     *      be intersected with the elements <code>Filter</code> to obtain the effective filter.
     * @param range The overall <code>DateRange</code> for the <code>Report</code>, or null if there is no
     *      overall date range.
     */
    void generate(User user, T element, Filter inheritedFilter, DateRange range);
}
