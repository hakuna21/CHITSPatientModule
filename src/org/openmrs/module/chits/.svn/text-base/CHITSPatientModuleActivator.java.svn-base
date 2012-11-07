package org.openmrs.module.chits;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class CHITSPatientModuleActivator extends BaseModuleActivator {
	/** Logger */
	private Log log = LogFactory.getLog(this.getClass());

	private static CHITSPatientModuleActivator instance;

	/** Items that need to be run when the context is refreshed */
	private static ThreadLocal<List<Runnable>> contextRefreshedTasks = new ThreadLocal<List<Runnable>>();

	public CHITSPatientModuleActivator() {
		instance = this;
	}

	public static CHITSPatientModuleActivator getInstance() {
		return instance;
	}

	@Override
	public void contextRefreshed() {
		super.contextRefreshed();

		// are there any tasks to run?
		final List<Runnable> tasks = contextRefreshedTasks.get();
		if (tasks != null) {
			// prepare to run tasks
			final ExecutorService exec = Executors.newFixedThreadPool(1);
			final List<Future<?>> futures = new ArrayList<Future<?>>();
			for (Runnable run : tasks) {
				exec.submit(run);
			}

			try {
				// wait forever for the tasks to complete
				exec.shutdown();

				for (Future<?> future : futures) {
					try {
						future.get();
					} catch (Exception ex) {
						log.error("Error in startup sequence", ex);
					}
				}

				// this should proceed without a hitch since we've obtained all 'future' references
				exec.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
			} catch (Exception ex) {
				log.error("Error in startup sequence", ex);
			} finally {
				tasks.clear();
			}
		}
	}

	/**
	 * @see org.openmrs.module.Activator#startup()
	 */
	@Override
	public void willStart() {
		log.info(String.format("Starting %s", getClass().getSimpleName()));
	}

	/**
	 * @see org.openmrs.module.Activator#shutdown()
	 */
	@Override
	public void willStop() {
		log.info(String.format("Shutting Down %s", getClass().getSimpleName()));
	}

	public void addContextRefreshedTask(Runnable run) {
		List<Runnable> tasks = contextRefreshedTasks.get();
		if (tasks == null) {
			contextRefreshedTasks.set(tasks = new ArrayList<Runnable>());
		}

		// add to set of tasks to run
		tasks.add(run);
	}
}
