/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2009-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.web.controller;

import java.util.List;

import org.opennms.web.api.Authentication;
import org.opennms.web.svclayer.SchedulerService;
import org.opennms.web.svclayer.model.ManageReportScheduleCommand;
import org.opennms.web.svclayer.model.TriggerDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * <p>ManageReportScheduleController class.</p>
 *
 * @author ranger
 * @version $Id: $
 * @since 1.8.1
 */
@Controller
@RequestMapping("/report/database/manageSchedule.htm")
public class ManageReportScheduleController {

    private final int m_pageSize = 10;

    @Autowired
    private SchedulerService m_reportSchedulerService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView listTriggers(@RequestParam(value = "p", required = false, defaultValue = "0") int page, ModelAndView modelAndView) {
        List<TriggerDescription> reportTriggers = m_reportSchedulerService.getTriggerDescriptions();

        PagedListHolder<TriggerDescription> pagedListHolder = new PagedListHolder<>(reportTriggers);
        pagedListHolder.setPageSize(m_pageSize);
        pagedListHolder.setPage(Math.max(page, 0)); // strip minus values

        modelAndView.addObject("pagedListHolder", pagedListHolder);
        modelAndView.addObject("command", new ManageReportScheduleCommand());

        modelAndView.setViewName("/report/database/manageSchedule");

        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView deleteTriggers(WebRequest request, @ModelAttribute ManageReportScheduleCommand command) {
        ModelAndView modelAndView = new ModelAndView();

        //we do not allow this as a read only user
        if (request.isUserInRole(Authentication.ROLE_READONLY)) {
            modelAndView.addObject("error", "You are a read only user and therefore not allowed to unschedule reports.");
            return listTriggers(0, modelAndView);
        } else {
            m_reportSchedulerService.removeTriggers(command.getTriggerNames());
            modelAndView.addObject("success", "Reports successfully unscheduled.");
            return listTriggers(0, modelAndView);
        }
    }

}
