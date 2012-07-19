/*
* PipelineFactory.java
*
* Created on 08 12 2011, 15:12
*
* Copyright (C) 2011 Alexey 'lh' Antonov
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ru.sincore.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.db.dao.PipelineRulesDAO;
import ru.sincore.db.dao.PipelineRulesDAOImpl;
import ru.sincore.db.pojo.PipelineRulesPOJO;
import ru.sincore.pipeline.processor.KickProcessor;
import ru.sincore.pipeline.processor.RemoveProcessor;
import ru.sincore.pipeline.processor.ReplaceProcessor;

import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Creates pipeline accordingly to information from db.<p/>
 * <b>Initialize factory by calling {@link PipelineFactory#initialize()} befor using it!</b>
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-12-08
 */
public class PipelineFactory
{
    private static final Logger log = LoggerFactory.getLogger(PipelineFactory.class);

    private static ConcurrentHashMap<String, Pipeline>  pipelines;
    private static ConcurrentHashMap<String, Class>     processors;

    private static boolean initialized;


    /**
     * Block for static initialization.
     */
    static
    {
        initialized = false;
        pipelines  = new ConcurrentHashMap<String, Pipeline>();
        processors = new ConcurrentHashMap<String, Class>();
    }


    public static Pipeline getPipeline(String pipelineName)
    {
        return pipelines.get(pipelineName);
    }


    public static Set<String> getProcessorsNames()
    {
        return processors.keySet();
    }


    public static void registerProcessor(String name, Class processorClass)
    {
        processors.put(name, processorClass);
    }
    
    
    public static void registerPipeline(String name, Pipeline pipeline)
    {
        pipelines.put(name, pipeline);
    }


    public static Processor createProcessor(String processorName)
    {
        Processor processor = null;
        try
        {
            Constructor
                    processorConstructor = processors.get(processorName).getConstructor();

            processorConstructor.setAccessible(true);

            processor = (Processor) processorConstructor.newInstance();
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
        }

        return processor;
    }
    
    
    private static void registerProcessors()
    {
        log.info("Start registering processors...");

        registerProcessor("replace", ReplaceProcessor.class);
        registerProcessor("remove",  RemoveProcessor.class);
        registerProcessor("kick",    KickProcessor.class);
    }


    private static void registerPipelineMSG()
    {
        log.info("Start registering MSG pipeline");

        Pipeline<MSG> msgPipeline = new Pipeline<MSG>();
        PipelineRulesDAO pipelineRulesDAO = new PipelineRulesDAOImpl("MSG");

        // add rules to pipeline
        for (PipelineRulesPOJO rules : pipelineRulesDAO.getRules())
        {
            try
            {
                Constructor
                        processorConstructor = processors.get(rules.getProcessor()).getConstructor();

                processorConstructor.setAccessible(true);

                Processor<MSG> processor = (Processor) processorConstructor.newInstance();

                processor.setMatcher(rules.getMatcher());
                processor.setParameter(rules.getParam());

                msgPipeline.addProcessor(processor);
            }
            catch (Exception ex)
            {
                log.error(ex.toString());
            }
        }

        pipelines.put("MSG", msgPipeline);
    }


    private static void registerPipelines()
    {
        log.info("Start registering pipelines...");

        registerPipelineMSG();
    }


    /**
     * Initialize pipeline factory:
     * <ul>
     *     <li>Registers all known processors</li>
     *     <li>Loads info from db and creates piplines</li>
     * </ul>
     */
    public static void initialize()
    {
        if (initialized)
        {
            log.info("Start reinitializing PipelineFactory...");

            initialized = false;

            processors.clear();
            pipelines.clear();
        }
        else
        {
            log.info("Start initializing PipelineFactory...");
        }

        registerProcessors();
        registerPipelines();
        initialized = true;
    }
}
