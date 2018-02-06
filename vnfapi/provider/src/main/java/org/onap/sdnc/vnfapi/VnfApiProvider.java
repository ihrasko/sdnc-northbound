/*-
 * ============LICENSE_START=======================================================
 * openECOMP : SDN-C
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights
 *                                                      reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.sdnc.vnfapi;

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.base.Optional;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataChangeListener;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.AsyncDataChangeEvent;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.OptimisticLockFailedException;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.NetworkTopologyOperationInput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.NetworkTopologyOperationInputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.NetworkTopologyOperationOutput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.NetworkTopologyOperationOutputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadNetworkTopologyOperationInput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadNetworkTopologyOperationInputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadNetworkTopologyOperationOutput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadNetworkTopologyOperationOutputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVfModuleTopologyOperationInput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVfModuleTopologyOperationInputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVfModuleTopologyOperationOutput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVfModuleTopologyOperationOutputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVfModules;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVfModulesBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfInstanceTopologyOperationInput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfInstanceTopologyOperationInputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfInstanceTopologyOperationOutput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfInstanceTopologyOperationOutputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfInstances;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfInstancesBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfTopologyOperationInput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfTopologyOperationInputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfTopologyOperationOutput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfTopologyOperationOutputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfs;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.PreloadVnfsBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VNFAPIService;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VfModuleTopologyOperationInput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VfModuleTopologyOperationInputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VfModuleTopologyOperationOutput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VfModuleTopologyOperationOutputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VfModules;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VfModulesBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfInstanceTopologyOperationInput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfInstanceTopologyOperationInputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfInstanceTopologyOperationOutput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfInstanceTopologyOperationOutputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfInstances;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfInstancesBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfTopologyOperationInput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfTopologyOperationInputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfTopologyOperationOutput;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfTopologyOperationOutputBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.Vnfs;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.VnfsBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.network.information.NetworkInformationBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.data.PreloadData;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.data.PreloadDataBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.model.information.VnfPreloadList;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.model.information.VnfPreloadListBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.model.information.VnfPreloadListKey;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.vf.module.model.information.VfModulePreloadList;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.vf.module.model.information.VfModulePreloadListBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.vf.module.model.information.VfModulePreloadListKey;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.vnf.instance.model.information.VnfInstancePreloadList;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.vnf.instance.model.information.VnfInstancePreloadListBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.preload.vnf.instance.model.information.VnfInstancePreloadListKey;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.request.information.RequestInformation;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.sdnc.request.header.SdncRequestHeader;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.sdnc.request.header.SdncRequestHeader.SvcAction;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.data.ServiceData;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.data.ServiceDataBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.status.ServiceStatus;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.status.ServiceStatus.RequestStatus;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.status.ServiceStatus.RpcAction;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.status.ServiceStatus.RpcName;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.status.ServiceStatus.VnfsdnAction;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.status.ServiceStatus.VnfsdnSubaction;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.service.status.ServiceStatusBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vf.module.information.VfModuleInformationBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vf.module.model.infrastructure.VfModuleList;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vf.module.model.infrastructure.VfModuleListBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vf.module.model.infrastructure.VfModuleListKey;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vf.module.preload.data.VfModulePreloadData;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vf.module.preload.data.VfModulePreloadDataBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vf.module.service.data.VfModuleServiceData;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vf.module.service.data.VfModuleServiceDataBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.information.VnfInformationBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.instance.information.VnfInstanceInformationBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.instance.model.infrastructure.VnfInstanceList;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.instance.model.infrastructure.VnfInstanceListBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.instance.model.infrastructure.VnfInstanceListKey;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.instance.preload.data.VnfInstancePreloadData;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.instance.preload.data.VnfInstancePreloadDataBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.instance.service.data.VnfInstanceServiceData;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.instance.service.data.VnfInstanceServiceDataBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.model.infrastructure.VnfList;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.model.infrastructure.VnfListBuilder;
import org.opendaylight.yang.gen.v1.org.onap.sdnctl.vnf.rev150720.vnf.model.infrastructure.VnfListKey;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.opendaylight.yangtools.yang.data.api.schema.tree.ModifiedNodeDoesNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.concurrent.Future;

/**
 * Defines a base implementation for your provider. This class extends from a helper class which provides storage for
 * the most commonly used components of the MD-SAL. Additionally the base class provides some basic logging and
 * initialization / clean up methods.
 */
public class VnfApiProvider implements AutoCloseable, VNFAPIService, DataChangeListener {

    private final Logger log = LoggerFactory.getLogger(VnfApiProvider.class);
    private final ExecutorService executor;

    private static final String APP_NAME = "vnfapi";
    private static final String VNF_API = "VNF-API";
    private static final String OPERATIONAL_DATA = "operational-data";

    private static final String READ_MD_SAL_STR = "Read MD-SAL (";
    private static final String DATA_FOR_STR = ") data for [";
    private static final String SERVICE_DATA_STR = "] ServiceData: ";
    private static final String NO_DATA_FOUND_STR = "No data found in MD-SAL (";
    private static final String EXCEPTION_READING_MD_SAL_STR = "Caught Exception reading MD-SAL (";
    private static final String FOR_STR = ") for [";
    private static final String INVALID_INPUT_VF_MODULE_STR = "invalid input, null or empty vf-module-id";
    private static final String UPDATED_MD_SAL_STR = "Updated MD-SAL for ";
    private static final String RETURNED_SUCCESS_STR = "Returned SUCCESS for ";
    private static final String UPDATING_OPERATIONAL_TREE_STR = "Updating OPERATIONAL tree.";
    private static final String UPDATING_MD_SAL_STR = "Updating MD-SAL for ";
    private static final String CAUGHT_EXCEPTION_STR = "Caught Exception updating MD-SAL for ";
    private static final String RETURNED_FAILED_STR = "Returned FAILED for ";
    private static final String ADDING_INPUT_DATA_STR = "Adding INPUT data for ";
    private static final String ADDING_OPERATIONAL_DATA_STR = "Adding OPERATIONAL data for ";
    private static final String OPERATIONAL_DATA_STR = "] operational-data: ";
    private static final String ADDING_CONFIG_DATA_STR = "Adding CONFIG data for ";
    private static final String INPUT_STR = "] input: ";
    private static final String CALLED_STR = " called.";
    private static final String EXITING_STR = "exiting ";
    private static final String INVALID_INPUT_VNF_INSTANCE_STR = "invalid input, null or empty vnf-instance-id";

    private VNFSDNSvcLogicServiceClient svcLogicClient;
    protected DataBroker dataBroker;
    protected NotificationPublishService notificationService;
    protected RpcProviderRegistry rpcRegistry;
    protected BindingAwareBroker.RpcRegistration<VNFAPIService> rpcRegistration;

    public VnfApiProvider(DataBroker dataBroker2, NotificationPublishService notificationPublishService,
        RpcProviderRegistry rpcProviderRegistry, VNFSDNSvcLogicServiceClient client) {
        log.info("Creating provider for " + APP_NAME);
        executor = Executors.newFixedThreadPool(1);
        dataBroker = dataBroker2;
        notificationService = notificationPublishService;
        rpcRegistry = rpcProviderRegistry;
        svcLogicClient = client;
        initialize();
    }

    private void initialize() {
        log.info("Initializing provider for " + APP_NAME);
        // Create the top level containers
        createContainers();
        try {
            VnfSdnUtil.loadProperties();
        } catch (Exception e) {
            log.error("Caught Exception while trying to load properties file: ", e);
        }

        log.info("Initialization complete for " + APP_NAME);
    }

    private void createContainers() {
        final WriteTransaction t = dataBroker.newReadWriteTransaction();

        // Create the Vnfs container
        t.merge(LogicalDatastoreType.CONFIGURATION, InstanceIdentifier.create(Vnfs.class), new VnfsBuilder().build());
        t.merge(LogicalDatastoreType.OPERATIONAL, InstanceIdentifier.create(Vnfs.class), new VnfsBuilder().build());

        // Create the PreloadVnfs container
        t.merge(LogicalDatastoreType.CONFIGURATION, InstanceIdentifier.create(PreloadVnfs.class),
            new PreloadVnfsBuilder().build());
        t.merge(LogicalDatastoreType.OPERATIONAL, InstanceIdentifier.create(PreloadVnfs.class),
            new PreloadVnfsBuilder().build());

        // 1610 Create the PreloadVnfInstances container
        t.merge(LogicalDatastoreType.CONFIGURATION, InstanceIdentifier.create(PreloadVnfInstances.class),
            new PreloadVnfInstancesBuilder().build());
        t.merge(LogicalDatastoreType.OPERATIONAL, InstanceIdentifier.create(PreloadVnfInstances.class),
            new PreloadVnfInstancesBuilder().build());

        // 1610 Create the VnfInstances container
        t.merge(LogicalDatastoreType.CONFIGURATION, InstanceIdentifier.create(VnfInstances.class),
            new VnfInstancesBuilder().build());
        t.merge(LogicalDatastoreType.OPERATIONAL, InstanceIdentifier.create(VnfInstances.class),
            new VnfInstancesBuilder().build());

        // 1610 Create the PreloadVfModules container
        t.merge(LogicalDatastoreType.CONFIGURATION, InstanceIdentifier.create(PreloadVfModules.class),
            new PreloadVfModulesBuilder().build());
        t.merge(LogicalDatastoreType.OPERATIONAL, InstanceIdentifier.create(PreloadVfModules.class),
            new PreloadVfModulesBuilder().build());

        // 1610 Create the VfModules container
        t.merge(LogicalDatastoreType.CONFIGURATION, InstanceIdentifier.create(VfModules.class),
            new VfModulesBuilder().build());
        t.merge(LogicalDatastoreType.OPERATIONAL, InstanceIdentifier.create(VfModules.class),
            new VfModulesBuilder().build());

        try {
            CheckedFuture<Void, TransactionCommitFailedException> checkedFuture = t.submit();
            checkedFuture.get();
            log.info("Create Containers succeeded!: ");

        } catch (InterruptedException | ExecutionException e) {
            log.error("Create Containers Failed: " + e);
        }
    }

    @Override
    public void close() throws Exception {
        log.info("Closing provider for " + APP_NAME);
        executor.shutdown();
        rpcRegistration.close();
        log.info("Successfully closed provider for " + APP_NAME);
    }

    // On data change not used
    @Override
    public void onDataChanged(AsyncDataChangeEvent<InstanceIdentifier<?>, DataObject> change) {

        log.info("   IN ON DATA CHANGE: ");

        boolean changed = false;
        WriteTransaction writeTransaction = dataBroker.newWriteOnlyTransaction();
        DataObject updatedSubTree = change.getUpdatedSubtree();

        if (updatedSubTree != null) {
            if (log.isDebugEnabled()) {
                log.debug("updatedSubTree was non-null:" + updatedSubTree);
            }
            if (updatedSubTree instanceof Vnfs) {
                changed = isChanged(changed, (Vnfs) updatedSubTree);
            }
            if (updatedSubTree instanceof PreloadVnfs) {
                changed = isChanged(changed, (PreloadVnfs) updatedSubTree);
            }
            //1610
            if (updatedSubTree instanceof PreloadVnfInstances) {
                changed = isChanged(changed, (PreloadVnfInstances) updatedSubTree);
            }
            //1610
            if (updatedSubTree instanceof VnfInstances) {
                changed = isChanged(changed, (VnfInstances) updatedSubTree);
            }
            //1610
            if (updatedSubTree instanceof PreloadVfModules) {
                changed = isChanged(changed, (PreloadVfModules) updatedSubTree);
            }
            //1610
            if (updatedSubTree instanceof VfModules) {
                changed = isChanged(changed, (VfModules) updatedSubTree);
            }
        }

        // Do the write transaction only if something changed.
        if (changed) {
            CheckedFuture<Void, TransactionCommitFailedException> checkedFuture = writeTransaction.submit();
            Futures.addCallback(checkedFuture, new FutureCallback<Void>() {

                @Override
                public void onSuccess(Void arg0) {
                    log.debug("Successfully updated Service Status");
                }

                @Override
                public void onFailure(Throwable e) {
                    log.debug("Failed updating Service Status", e);
                }
            }, executor);
        }
    }

    private boolean isChanged(boolean changed, VfModules updatedSubTree) {
        ArrayList<VfModuleList> vfModuleList =
            (ArrayList<VfModuleList>) updatedSubTree.getVfModuleList();
        if (vfModuleList != null) {
            for (VfModuleList entry : vfModuleList) {
                VfModuleServiceData vfModuleServiceData = entry.getVfModuleServiceData();
                ServiceStatus serviceStatus = entry.getServiceStatus();
                if (vfModuleServiceData != null && serviceStatus != null) {
                    // VfModuleServiceData change detected, check the AckFinal indicator and request-status to
                    // see if we need to proceed.
                    return isChanged(changed, serviceStatus);
                }
            }
        }
        return changed;
    }

    private boolean isChanged(boolean changed, PreloadVfModules updatedSubTree) {
        ArrayList<VfModulePreloadList> vnfInstanceList =
            (ArrayList<VfModulePreloadList>) updatedSubTree.getVfModulePreloadList();
        if (vnfInstanceList != null) {
            for (VfModulePreloadList entry : vnfInstanceList) {
                VfModulePreloadData vnfInstancePreloadData = entry.getVfModulePreloadData();
                if (vnfInstancePreloadData != null) {
                    return true;
                }
            }
        }
        return changed;
    }

    private boolean isChanged(boolean changed, VnfInstances updatedSubTree) {
        ArrayList<VnfInstanceList> vnfInstanceList =
            (ArrayList<VnfInstanceList>) updatedSubTree.getVnfInstanceList();
        if (vnfInstanceList != null) {
            for (VnfInstanceList entry : vnfInstanceList) {
                VnfInstanceServiceData vnfInstanceServiceData = entry.getVnfInstanceServiceData();
                ServiceStatus serviceStatus = entry.getServiceStatus();
                if (vnfInstanceServiceData != null && serviceStatus != null) {
                    // VnfInstanceServiceData change detected, check the AckFinal indicator and request-status
                    // to see if we need to proceed.
                    return isChanged(changed, serviceStatus);
                }
            }
        }
        return changed;
    }

    private boolean isChanged(boolean changed, PreloadVnfInstances updatedSubTree) {
        ArrayList<VnfInstancePreloadList> vnfInstanceList =
            (ArrayList<VnfInstancePreloadList>) updatedSubTree
                .getVnfInstancePreloadList();
        if (vnfInstanceList != null) {
            for (VnfInstancePreloadList entry : vnfInstanceList) {
                VnfInstancePreloadData vnfInstancePreloadData = entry.getVnfInstancePreloadData();
                if (vnfInstancePreloadData != null) {
                    return true;
                }
            }
        }
        return changed;
    }

    private boolean isChanged(boolean changed, PreloadVnfs updatedSubTree) {
        ArrayList<VnfPreloadList> vnfList =
            (ArrayList<VnfPreloadList>) updatedSubTree.getVnfPreloadList();
        if (vnfList != null) {
            for (VnfPreloadList entry : vnfList) {
                PreloadData preloadData = entry.getPreloadData();
                if (preloadData != null) {
                    return true;
                }
            }
        }
        return changed;
    }

    private boolean isChanged(boolean changed, Vnfs updatedSubTree) {
        ArrayList<VnfList> vnfList = (ArrayList<VnfList>) updatedSubTree.getVnfList();
        if (vnfList != null) {
            for (VnfList entry : vnfList) {
                ServiceData serviceData = entry.getServiceData();
                ServiceStatus serviceStatus = entry.getServiceStatus();
                if (serviceData != null && serviceStatus != null) {
                    //
                    // ServiceData change detected, check the AckFinal indicator and request-status to see if we need to proceed.
                    //
                    return isChanged(changed, serviceStatus);
                }
            }
        }
        return changed;
    }

    private boolean isChanged(boolean changed, ServiceStatus serviceStatus) {
        if ((!"Y".equals(serviceStatus.getFinalIndicator())) && (RequestStatus.Synccomplete
            .equals(serviceStatus.getRequestStatus()))) {
            if (log.isDebugEnabled()) {
                log.debug("Final Indicator is not Y, calling handleServiceDataUpdated");
            }
            return true;
        }
        return changed;
    }

    private static class Iso8601Util {


        private static TimeZone tz = TimeZone.getTimeZone("UTC");
        private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        private Iso8601Util() {
        }

        static {
            df.setTimeZone(tz);
        }

        private static String now() {
            return df.format(new Date());
        }
    }

    private void setServiceStatus(ServiceStatusBuilder serviceStatusBuilder, String errorCode, String errorMessage,
        String ackFinal) {
        serviceStatusBuilder.setResponseCode(errorCode);
        serviceStatusBuilder.setResponseMessage(errorMessage);
        serviceStatusBuilder.setFinalIndicator(ackFinal);
        serviceStatusBuilder.setResponseTimestamp(Iso8601Util.now());
    }

    private void setServiceStatus(ServiceStatusBuilder serviceStatusBuilder, RequestInformation requestInformation) {
        if (requestInformation != null && requestInformation.getRequestAction() != null) {
            switch (requestInformation.getRequestAction()) {
                case VNFActivateRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.VNFActivateRequest);
                    break;
                case ChangeVNFActivateRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.ChangeVNFActivateRequest);
                    break;
                case DisconnectVNFRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.DisconnectVNFRequest);
                    break;
                case PreloadVNFRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.PreloadVNFRequest);
                    break;
                case DeletePreloadVNFRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.DeletePreloadVNFRequest);
                    break;
                // 1610 vnf-instance Requests
                case VnfInstanceActivateRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.VnfInstanceActivateRequest);
                    break;
                case ChangeVnfInstanceActivateRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.ChangeVnfInstanceActivateRequest);
                    break;
                case DisconnectVnfInstanceRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.DisconnectVnfInstanceRequest);
                    break;
                case PreloadVnfInstanceRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.PreloadVnfInstanceRequest);
                    break;
                // 1610 vf-module Requests
                case VfModuleActivateRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.VfModuleActivateRequest);
                    break;
                case ChangeVfModuleActivateRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.ChangeVfModuleActivateRequest);
                    break;
                case DisconnectVfModuleRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.DisconnectVfModuleRequest);
                    break;
                case PreloadVfModuleRequest:
                    serviceStatusBuilder.setVnfsdnAction(VnfsdnAction.PreloadVfModuleRequest);
                    break;
                default:
                    log.error("Unknown RequestAction: " + requestInformation.getRequestAction());
                    break;
            }
        }
        if (requestInformation != null && requestInformation.getRequestSubAction() != null) {
            switch (requestInformation.getRequestSubAction()) {
                case SUPP:
                    serviceStatusBuilder.setVnfsdnSubaction(VnfsdnSubaction.SUPP);
                    break;
                case CANCEL:
                    serviceStatusBuilder.setVnfsdnSubaction(VnfsdnSubaction.CANCEL);
                    break;
                default:
                    log.error("Unknown RequestSubAction: " + requestInformation.getRequestSubAction());
                    break;
            }
        }
    }

    private void setServiceStatus(ServiceStatusBuilder serviceStatusBuilder, SdncRequestHeader requestHeader) {
        if (requestHeader != null && requestHeader.getSvcAction() != null) {
            switch (requestHeader.getSvcAction()) {
                case Reserve:
                    serviceStatusBuilder.setRpcAction(RpcAction.Reserve);
                    break;
                case Activate:
                    serviceStatusBuilder.setRpcAction(RpcAction.Activate);
                    break;
                case Assign:
                    serviceStatusBuilder.setRpcAction(RpcAction.Assign);
                    break;
                case Delete:
                    serviceStatusBuilder.setRpcAction(RpcAction.Delete);
                    break;
                case Changeassign:
                    serviceStatusBuilder.setRpcAction(RpcAction.Changeassign);
                    break;
                case Changedelete:
                    serviceStatusBuilder.setRpcAction(RpcAction.Changedelete);
                    break;
                case Rollback:
                    serviceStatusBuilder.setRpcAction(RpcAction.Rollback);
                    break;
                default:
                    log.error("Unknown SvcAction: " + requestHeader.getSvcAction());
                    break;
            }
        }
    }

    private void getServiceData(String siid, ServiceDataBuilder serviceDataBuilder) {
        // default to config
        getServiceData(siid, serviceDataBuilder, LogicalDatastoreType.CONFIGURATION);
    }


    private void getServiceData(String siid, ServiceDataBuilder serviceDataBuilder, LogicalDatastoreType type) {
        // See if any data exists yet for this siid, if so grab it.
        InstanceIdentifier<VnfList> serviceInstanceIdentifier = InstanceIdentifier
            .builder(Vnfs.class)
            .child(VnfList.class, new VnfListKey(siid))
            .build();

        ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
        Optional<VnfList> data = Optional.absent();
        try {
            data = readTx.read(type, serviceInstanceIdentifier).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(EXCEPTION_READING_MD_SAL_STR + type + FOR_STR + siid + "] ", e);
        }

        if (data.isPresent()) {
            ServiceData serviceData = (ServiceData) data.get().getServiceData();
            if (serviceData != null) {
                log.info(READ_MD_SAL_STR + type + DATA_FOR_STR + siid + SERVICE_DATA_STR + serviceData);
                serviceDataBuilder.setSdncRequestHeader(serviceData.getSdncRequestHeader());
                serviceDataBuilder.setRequestInformation(serviceData.getRequestInformation());
                serviceDataBuilder.setServiceInformation(serviceData.getServiceInformation());
                serviceDataBuilder.setVnfRequestInformation(serviceData.getVnfRequestInformation());
                serviceDataBuilder.setVnfId(serviceData.getVnfId());
                serviceDataBuilder.setVnfTopologyInformation(serviceData.getVnfTopologyInformation());
                serviceDataBuilder.setOperStatus(serviceData.getOperStatus());
            } else {
                log.info("No service-data found in MD-SAL (" + type + FOR_STR + siid + "] ");
            }
        } else {
            log.info(NO_DATA_FOUND_STR + type + FOR_STR + siid + "] ");
        }
    }

    //1610 vnf-instance
    private void getVnfInstanceServiceData(String siid, VnfInstanceServiceDataBuilder vnfInstanceServiceDataBuilder) {
        // default to config
        getVnfInstanceServiceData(siid, vnfInstanceServiceDataBuilder, LogicalDatastoreType.CONFIGURATION);
    }

    //1610 vnf-instance
    private void getVnfInstanceServiceData(String siid, VnfInstanceServiceDataBuilder vnfInstanceServiceDataBuilder,
        LogicalDatastoreType type) {
        // See if any data exists yet for this siid, if so grab it.
        InstanceIdentifier<VnfInstanceList> vnfInstanceIdentifier = InstanceIdentifier
            .builder(VnfInstances.class)
            .child(VnfInstanceList.class, new VnfInstanceListKey(siid))
            .build();

        ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
        Optional<VnfInstanceList> data = Optional.absent();
        try {
            data = readTx.read(type, vnfInstanceIdentifier).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(EXCEPTION_READING_MD_SAL_STR + type + FOR_STR + siid + "] ", e);
        }

        if (data.isPresent()) {
            VnfInstanceServiceData vnfInstanceServiceData = data.get().getVnfInstanceServiceData();
            if (vnfInstanceServiceData != null) {
                log.info(READ_MD_SAL_STR + type + DATA_FOR_STR + siid + "] VnfInstanceServiceData: "
                    + vnfInstanceServiceData);
                vnfInstanceServiceDataBuilder.setSdncRequestHeader(vnfInstanceServiceData.getSdncRequestHeader());
                vnfInstanceServiceDataBuilder.setRequestInformation(vnfInstanceServiceData.getRequestInformation());
                vnfInstanceServiceDataBuilder.setServiceInformation(vnfInstanceServiceData.getServiceInformation());
                vnfInstanceServiceDataBuilder
                    .setVnfInstanceRequestInformation(vnfInstanceServiceData.getVnfInstanceRequestInformation());
                vnfInstanceServiceDataBuilder.setVnfInstanceId(vnfInstanceServiceData.getVnfInstanceId());
                vnfInstanceServiceDataBuilder
                    .setVnfInstanceTopologyInformation(vnfInstanceServiceData.getVnfInstanceTopologyInformation());
                vnfInstanceServiceDataBuilder.setOperStatus(vnfInstanceServiceData.getOperStatus());
            } else {
                log.info("No vnf-instance-service-data found in MD-SAL (" + type + FOR_STR + siid + "] ");
            }
        } else {
            log.info(NO_DATA_FOUND_STR + type + FOR_STR + siid + "] ");
        }
    }

    //1610 vf-module
    private void getVfModuleServiceData(String siid, VfModuleServiceDataBuilder vfModuleServiceDataBuilder) {
        // default to config
        getVfModuleServiceData(siid, vfModuleServiceDataBuilder, LogicalDatastoreType.CONFIGURATION);
    }

    //1610 vf-module
    private void getVfModuleServiceData(String siid, VfModuleServiceDataBuilder vfModuleServiceDataBuilder,
        LogicalDatastoreType type) {
        // See if any data exists yet for this siid, if so grab it.
        InstanceIdentifier<VfModuleList> vfModuleIdentifier = InstanceIdentifier
            .builder(VfModules.class)
            .child(VfModuleList.class, new VfModuleListKey(siid))
            .build();

        ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
        Optional<VfModuleList> data = Optional.absent();
        try {
            data = readTx.read(type, vfModuleIdentifier).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(EXCEPTION_READING_MD_SAL_STR + type + FOR_STR + siid + "] ", e);
        }

        if (data.isPresent()) {
            VfModuleServiceData vfModuleServiceData = data.get().getVfModuleServiceData();
            if (vfModuleServiceData != null) {
                log.info(
                    READ_MD_SAL_STR + type + DATA_FOR_STR + siid + "] VfModuleServiceData: " + vfModuleServiceData);
                vfModuleServiceDataBuilder.setSdncRequestHeader(vfModuleServiceData.getSdncRequestHeader());
                vfModuleServiceDataBuilder.setRequestInformation(vfModuleServiceData.getRequestInformation());
                vfModuleServiceDataBuilder.setServiceInformation(vfModuleServiceData.getServiceInformation());
                vfModuleServiceDataBuilder
                    .setVfModuleRequestInformation(vfModuleServiceData.getVfModuleRequestInformation());
                vfModuleServiceDataBuilder.setVfModuleId(vfModuleServiceData.getVfModuleId());
                vfModuleServiceDataBuilder
                    .setVfModuleTopologyInformation(vfModuleServiceData.getVfModuleTopologyInformation());
                vfModuleServiceDataBuilder.setOperStatus(vfModuleServiceData.getOperStatus());
            } else {
                log.info("No vf-module-service-data found in MD-SAL (" + type + FOR_STR + siid + "] ");
            }
        } else {
            log.info(NO_DATA_FOUND_STR + type + FOR_STR + siid + "] ");
        }
    }


    private void getPreloadData(String vnfName, String vnfType, PreloadDataBuilder preloadDataBuilder) {
        // default to config
        getPreloadData(vnfName, vnfType, preloadDataBuilder, LogicalDatastoreType.CONFIGURATION);
    }

    private void getPreloadData(String preloadName, String preloadType, PreloadDataBuilder preloadDataBuilder,
        LogicalDatastoreType type) {
        // See if any data exists yet for this name/type, if so grab it.
        InstanceIdentifier<VnfPreloadList> preloadInstanceIdentifier = InstanceIdentifier
            .builder(PreloadVnfs.class)
            .child(VnfPreloadList.class, new VnfPreloadListKey(preloadName, preloadType))
            .build();

        ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
        Optional<VnfPreloadList> data = Optional.absent();
        try {
            data = readTx.read(type, preloadInstanceIdentifier).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(EXCEPTION_READING_MD_SAL_STR + type + FOR_STR + preloadName + "," + preloadType + "] ",
                e);
        }

        if (data.isPresent()) {
            PreloadData preloadData = (PreloadData) data.get().getPreloadData();
            if (preloadData != null) {
                log.info(READ_MD_SAL_STR + type + DATA_FOR_STR + preloadName + "," + preloadType + "] PreloadData: "
                    + preloadData);
                preloadDataBuilder.setVnfTopologyInformation(preloadData.getVnfTopologyInformation());
                preloadDataBuilder.setNetworkTopologyInformation(preloadData.getNetworkTopologyInformation());
                preloadDataBuilder.setOperStatus(preloadData.getOperStatus());
            } else {
                log.info(
                    "No preload-data found in MD-SAL (" + type + FOR_STR + preloadName + "," + preloadType + "] ");
            }
        } else {
            log.info(NO_DATA_FOUND_STR + type + FOR_STR + preloadName + "," + preloadType + "] ");
        }
    }

    //1610 preload-vnf-instance
    private void getVnfInstancePreloadData(String vnfName, String vnfType,
        VnfInstancePreloadDataBuilder preloadDataBuilder) {
        // default to config
        getVnfInstancePreloadData(vnfName, vnfType, preloadDataBuilder, LogicalDatastoreType.CONFIGURATION);
    }

    //1610 preload-vnf-instance
    private void getVnfInstancePreloadData(String preloadName, String preloadType,
        VnfInstancePreloadDataBuilder preloadDataBuilder, LogicalDatastoreType type) {
        // See if any data exists yet for this name/type, if so grab it.
        InstanceIdentifier<VnfInstancePreloadList> preloadInstanceIdentifier = InstanceIdentifier
            .builder(PreloadVnfInstances.class)
            .child(VnfInstancePreloadList.class, new VnfInstancePreloadListKey(preloadName, preloadType))
            .build();

        ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
        Optional<VnfInstancePreloadList> data = Optional.absent();
        try {
            data = readTx.read(type, preloadInstanceIdentifier).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(EXCEPTION_READING_MD_SAL_STR + type + FOR_STR + preloadName + "," + preloadType + "] ",
                e);
        }

        if (data.isPresent()) {
            VnfInstancePreloadData preloadData = (VnfInstancePreloadData) data.get().getVnfInstancePreloadData();
            if (preloadData != null) {
                log.info(READ_MD_SAL_STR + type + DATA_FOR_STR + preloadName + "," + preloadType
                    + "] VnfInstancePreloadData: " + preloadData);
                preloadDataBuilder.setVnfInstanceTopologyInformation(preloadData.getVnfInstanceTopologyInformation());
                preloadDataBuilder.setOperStatus(preloadData.getOperStatus());
            } else {
                log.info("No vnf-instance-preload-data found in MD-SAL (" + type + FOR_STR + preloadName + ","
                    + preloadType + "] ");
            }
        } else {
            log.info(NO_DATA_FOUND_STR + type + FOR_STR + preloadName + "," + preloadType + "] ");
        }
    }

    // 1610 preload-vf-module
    private void getVfModulePreloadData(String vnfName, String vnfType,
        VfModulePreloadDataBuilder preloadDataBuilder) {
        // default to config
        getVfModulePreloadData(vnfName, vnfType, preloadDataBuilder, LogicalDatastoreType.CONFIGURATION);
    }

    private void getVfModulePreloadData(String preloadName, String preloadType,
        VfModulePreloadDataBuilder preloadDataBuilder, LogicalDatastoreType type) {
        // See if any data exists yet for this name/type, if so grab it.
        InstanceIdentifier<VfModulePreloadList> preloadInstanceIdentifier = InstanceIdentifier
            .builder(PreloadVfModules.class)
            .child(VfModulePreloadList.class, new VfModulePreloadListKey(preloadName, preloadType))
            .build();

        ReadOnlyTransaction readTx = dataBroker.newReadOnlyTransaction();
        Optional<VfModulePreloadList> data = Optional.absent();

        try {
            data = readTx.read(type, preloadInstanceIdentifier).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(EXCEPTION_READING_MD_SAL_STR + type + FOR_STR + preloadName + "," + preloadType + "] ",
                e);
        }

        if (data.isPresent()) {
            VfModulePreloadData preloadData = (VfModulePreloadData) data.get().getVfModulePreloadData();
            if (preloadData != null) {
                log.info(READ_MD_SAL_STR + type + DATA_FOR_STR + preloadName + "," + preloadType
                    + "] VfModulePreloadData: " + preloadData);
                preloadDataBuilder.setVfModuleTopologyInformation(preloadData.getVfModuleTopologyInformation());
                preloadDataBuilder.setOperStatus(preloadData.getOperStatus());
            } else {
                log.info(
                    "No preload-data found in MD-SAL (" + type + FOR_STR + preloadName + "," + preloadType + "] ");
            }
        } else {
            log.info(NO_DATA_FOUND_STR + type + FOR_STR + preloadName + "," + preloadType + "] ");
        }
    }

    private void deleteVnfList(final VnfList entry, LogicalDatastoreType storeType) {
        // Each entry will be identifiable by a unique key, we have to create that identifier
        InstanceIdentifier<VnfList> path = InstanceIdentifier
            .builder(Vnfs.class)
            .child(VnfList.class, entry.getKey())
            .build();

        int optimisticLockTries = 2;
        boolean tryAgain = true;
        while (tryAgain) {
            tryAgain = false;
            try {
                WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
                tx.delete(storeType, path);
                tx.submit().checkedGet();
                log.debug("DataStore delete succeeded");
            } catch (OptimisticLockFailedException e) {
                if (--optimisticLockTries <= 0) {
                    log.debug("Got OptimisticLockFailedException on last try - failing ");
                    throw new IllegalStateException(e);
                }
                log.debug("Got OptimisticLockFailedException - trying again ");
                tryAgain = true;

            } catch (final TransactionCommitFailedException e) {

                if (e.getCause() instanceof ModifiedNodeDoesNotExistException) {
                    log.debug("Ignoring MpdifiedNodeDoesNotExistException");
                    break;
                }

                log.debug("Delete DataStore failed");
                throw new IllegalStateException(e);
            }
        }
    }

    private void saveVnfList(final VnfList entry, boolean merge, LogicalDatastoreType storeType) {
        // Each entry will be identifiable by a unique key, we have to create that identifier
        InstanceIdentifier<VnfList> path = InstanceIdentifier
            .builder(Vnfs.class)
            .child(VnfList.class, entry.getKey())
            .build();

        tryUpdateDataStore(entry, merge, storeType, path);
    }

    //1610 vnf-instance
    private void saveVnfInstanceList(final VnfInstanceList entry, boolean merge, LogicalDatastoreType storeType) {
        // Each entry will be identifiable by a unique key, we have to create that identifier
        InstanceIdentifier<VnfInstanceList> path = InstanceIdentifier
            .builder(VnfInstances.class)
            .child(VnfInstanceList.class, entry.getKey())
            .build();

        tryUpdateDataStore(entry, merge, storeType, path);
    }

    //1610 vf-module
    private void saveVfModuleList(final VfModuleList entry, boolean merge, LogicalDatastoreType storeType) {
        // Each entry will be identifiable by a unique key, we have to create that identifier
        InstanceIdentifier<VfModuleList> path = InstanceIdentifier
            .builder(VfModules.class)
            .child(VfModuleList.class, entry.getKey())
            .build();

        tryUpdateDataStore(entry, merge, storeType, path);
    }

    private void savePreloadList(final VnfPreloadList entry, boolean merge, LogicalDatastoreType storeType) {

        // Each entry will be identifiable by a unique key, we have to create that identifier
        InstanceIdentifier<VnfPreloadList> path = InstanceIdentifier
            .builder(PreloadVnfs.class)
            .child(VnfPreloadList.class, entry.getKey())
            .build();

        tryUpdateDataStore(entry, merge, storeType, path);
    }

    //1610 preload vnf-instance
    private void saveVnfInstancePreloadList(final VnfInstancePreloadList entry, boolean merge,
        LogicalDatastoreType storeType) {

        // Each entry will be identifiable by a unique key, we have to create that identifier
        InstanceIdentifier<VnfInstancePreloadList> path = InstanceIdentifier
            .builder(PreloadVnfInstances.class)
            .child(VnfInstancePreloadList.class, entry.getKey())
            .build();

        tryUpdateDataStore(entry, merge, storeType, path);
    }

    //1610 preload vf-module
    private void saveVfModulePreloadList(final VfModulePreloadList entry, boolean merge,
        LogicalDatastoreType storeType) {

        // Each entry will be identifiable by a unique key, we have to create that identifier
        InstanceIdentifier<VfModulePreloadList> path = InstanceIdentifier
            .builder(PreloadVfModules.class)
            .child(VfModulePreloadList.class, entry.getKey())
            .build();

        tryUpdateDataStore(entry, merge, storeType, path);
    }

    private <T extends DataObject> void tryUpdateDataStore(T entry, boolean merge, LogicalDatastoreType storeType,
        InstanceIdentifier<T> path) {

        int tries = 2;
        while (true) {
            try {
                WriteTransaction tx = dataBroker.newWriteOnlyTransaction();
                if (merge) {
                    tx.merge(storeType, path, entry);
                } else {
                    tx.put(storeType, path, entry);
                }
                tx.submit().checkedGet();
                log.debug("Update DataStore succeeded");
                break;
            } catch (OptimisticLockFailedException e) {
                if (--tries <= 0) {
                    log.debug("Got OptimisticLockFailedException on last try - failing ");
                    throw new IllegalStateException(e);
                }
                log.debug("Got OptimisticLockFailedException - trying again ");
            } catch (final TransactionCommitFailedException e) {
                log.debug("Update DataStore failed");
                throw new IllegalStateException(e);
            }
        }
    }

    //Save the requestId into MDC
    private void setRequestIdAsMDC(String requestId) {
        MDC.put("RequestId", requestId);
    }

    //1610 vnf-instance-topology-operation
    @Override
    public Future<RpcResult<VnfInstanceTopologyOperationOutput>> vnfInstanceTopologyOperation(
        VnfInstanceTopologyOperationInput input) {

        final String svcOperation = "vnf-instance-topology-operation";
        VnfInstanceServiceData vnfInstanceServiceData;
        ServiceStatusBuilder serviceStatusBuilder = new ServiceStatusBuilder();
        Properties parms = new Properties();

        log.info(svcOperation + CALLED_STR);
        // create a new response object
        VnfInstanceTopologyOperationOutputBuilder responseBuilder = new VnfInstanceTopologyOperationOutputBuilder();

        if (input == null || input.getVnfInstanceRequestInformation() == null
            || input.getVnfInstanceRequestInformation().getVnfInstanceId() == null) {
            log.debug(EXITING_STR + svcOperation + " because of " + INVALID_INPUT_VNF_INSTANCE_STR);
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage(INVALID_INPUT_VNF_INSTANCE_STR);
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<VnfInstanceTopologyOperationOutput> rpcResult = RpcResultBuilder
                .<VnfInstanceTopologyOperationOutput>status(true)
                .withResult(responseBuilder.build())
                .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Grab the service instance ID from the input buffer
        String viid = input.getVnfInstanceRequestInformation().getVnfInstanceId();
        String preloadName = input.getVnfInstanceRequestInformation().getVnfInstanceName();
        String preloadType = input.getVnfInstanceRequestInformation().getVnfModelId();

        // Make sure we have a valid viid
        if (viid == null || viid.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid vnf-instance-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage(INVALID_INPUT_VNF_INSTANCE_STR);
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<VnfInstanceTopologyOperationOutput> rpcResult = RpcResultBuilder
                .<VnfInstanceTopologyOperationOutput>status(true)
                .withResult(responseBuilder.build())
                .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        if (input.getSdncRequestHeader() != null) {
            responseBuilder.setSvcRequestId(input.getSdncRequestHeader().getSvcRequestId());
            setRequestIdAsMDC(input.getSdncRequestHeader().getSvcRequestId());
        }

        // Get vnf-instance-preload-data
        VnfInstancePreloadDataBuilder vnfInstancePreloadDataBuilder = new VnfInstancePreloadDataBuilder();
        getVnfInstancePreloadData(preloadName, preloadType, vnfInstancePreloadDataBuilder);

        // Get service-data
        VnfInstanceServiceDataBuilder vnfInstanceServiceDataBuilder = new VnfInstanceServiceDataBuilder();
        getVnfInstanceServiceData(viid, vnfInstanceServiceDataBuilder);

        // Get operational-data
        VnfInstanceServiceDataBuilder operDataBuilder = new VnfInstanceServiceDataBuilder();
        getVnfInstanceServiceData(viid, operDataBuilder, LogicalDatastoreType.OPERATIONAL);

        // Set the serviceStatus based on input
        setServiceStatus(serviceStatusBuilder, input.getSdncRequestHeader());
        setServiceStatus(serviceStatusBuilder, input.getRequestInformation());

        //
        // setup a service-data object builder
        // ACTION vnf-topology-operationa
        // INPUT:
        //  USES sdnc-request-header;
        //  USES request-information;
        //  USES service-information;
        //  USES vnf-request-information
        // OUTPUT:
        //  USES vnf-topology-response-body;
        //  USES vnf-information
        //  USES service-information
        //
        // container service-data
        //   uses vnf-configuration-information;
        //   uses oper-status;

        log.info(ADDING_INPUT_DATA_STR + svcOperation + " [" + viid + INPUT_STR + input);
        VnfInstanceTopologyOperationInputBuilder inputBuilder = new VnfInstanceTopologyOperationInputBuilder(input);
        VnfSdnUtil.toProperties(parms, inputBuilder.build());

        log.info(ADDING_OPERATIONAL_DATA_STR + svcOperation + " [" + viid + OPERATIONAL_DATA_STR + operDataBuilder
            .build());
        VnfSdnUtil.toProperties(parms, OPERATIONAL_DATA, operDataBuilder);

        log.info(
            ADDING_CONFIG_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType + "] preload-data: "
                + vnfInstancePreloadDataBuilder.build());
        VnfSdnUtil.toProperties(parms, "vnf-instance-preload-data", vnfInstancePreloadDataBuilder);

        // Call SLI sync method
        // Get SvcLogicService reference
        Properties respProps = null;
        String errorCode = "200";
        String errorMessage = null;
        String ackFinal = "Y";

        try {
            if (svcLogicClient.hasGraph(VNF_API, svcOperation, null, "sync")) {

                try {
                    respProps = svcLogicClient
                        .execute(VNF_API, svcOperation, null, "sync", vnfInstanceServiceDataBuilder, parms);
                } catch (Exception e) {
                    log.error("Caught exception executing service logic for " + svcOperation, e);
                    errorMessage = e.getMessage();
                    errorCode = "500";
                }
            } else {
                errorMessage = "No service logic active for VNF-API: '" + svcOperation + "'";
                errorCode = "503";
            }
        } catch (Exception e) {
            errorCode = "500";
            errorMessage = e.getMessage();
            log.error("Caught exception looking for service logic", e);
        }

        if (respProps != null) {
            errorCode = respProps.getProperty("error-code");
            errorMessage = respProps.getProperty("error-message");
            ackFinal = respProps.getProperty("ack-final", "Y");
        }

        setServiceStatus(serviceStatusBuilder, errorCode, errorMessage, ackFinal);
        serviceStatusBuilder.setRequestStatus(RequestStatus.Synccomplete);
        serviceStatusBuilder.setRpcName(RpcName.VnfInstanceTopologyOperation);

        if (errorCode != null && errorCode.length() != 0 && !(errorCode.equals("0") || errorCode.equals("200"))) {
            responseBuilder.setResponseCode(errorCode);
            responseBuilder.setResponseMessage(errorMessage);
            responseBuilder.setAckFinalIndicator(ackFinal);
            VnfInstanceListBuilder vnfInstanceListBuilder = new VnfInstanceListBuilder();
            vnfInstanceListBuilder.setVnfInstanceId(viid);
            vnfInstanceListBuilder.setServiceStatus(serviceStatusBuilder.build());
            try {
                saveVnfInstanceList(vnfInstanceListBuilder.build(), true, LogicalDatastoreType.CONFIGURATION);
            } catch (Exception e) {
                log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + viid + "] \n", e);
            }
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + viid + "] " + responseBuilder.build());
            RpcResult<VnfInstanceTopologyOperationOutput> rpcResult = RpcResultBuilder
                .<VnfInstanceTopologyOperationOutput>status(true)
                .withResult(responseBuilder.build())
                .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Got success from SLI
        try {
            vnfInstanceServiceData = vnfInstanceServiceDataBuilder.build();
            log.info(UPDATING_MD_SAL_STR + svcOperation + " [" + viid + "] VnfInstanceServiceData: "
                + vnfInstanceServiceData);
            // svc-configuration-list
            VnfInstanceListBuilder vnfInstanceListBuilder = new VnfInstanceListBuilder();
            vnfInstanceListBuilder.setVnfInstanceServiceData(vnfInstanceServiceData);
            vnfInstanceListBuilder.setVnfInstanceId(vnfInstanceServiceData.getVnfInstanceId());
            vnfInstanceListBuilder.setServiceStatus(serviceStatusBuilder.build());
            saveVnfInstanceList(vnfInstanceListBuilder.build(), false, LogicalDatastoreType.CONFIGURATION);
            if (input.getSdncRequestHeader() != null && input.getSdncRequestHeader().getSvcAction() != null) {
                // Only update operational tree on Delete or Activate
                if (input.getSdncRequestHeader().getSvcAction().equals(SvcAction.Delete) || input.getSdncRequestHeader()
                    .getSvcAction().equals(SvcAction.Activate)) {
                    log.info(UPDATING_OPERATIONAL_TREE_STR);
                    saveVnfInstanceList(vnfInstanceListBuilder.build(), false, LogicalDatastoreType.OPERATIONAL);
                }
            }
            VnfInstanceInformationBuilder vnfInstanceInformationBuilder = new VnfInstanceInformationBuilder();
            vnfInstanceInformationBuilder.setVnfInstanceId(viid);
            responseBuilder.setVnfInstanceInformation(vnfInstanceInformationBuilder.build());
            responseBuilder.setServiceInformation(vnfInstanceServiceData.getServiceInformation());
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + viid + "] \n", e);
            responseBuilder.setResponseCode("500");
            responseBuilder.setResponseMessage(e.toString());
            responseBuilder.setAckFinalIndicator("Y");
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + viid + "] " + responseBuilder.build());
            RpcResult<VnfInstanceTopologyOperationOutput> rpcResult = RpcResultBuilder
                .<VnfInstanceTopologyOperationOutput>status(true)
                .withResult(responseBuilder.build())
                .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Update succeeded
        responseBuilder.setResponseCode(errorCode);
        responseBuilder.setAckFinalIndicator(ackFinal);
        if (errorMessage != null) {
            responseBuilder.setResponseMessage(errorMessage);
        }
        log.info(UPDATED_MD_SAL_STR + svcOperation + " [" + viid + "] ");
        log.info(RETURNED_SUCCESS_STR + svcOperation + " [" + viid + "] " + responseBuilder.build());

        RpcResult<VnfInstanceTopologyOperationOutput> rpcResult = RpcResultBuilder
            .<VnfInstanceTopologyOperationOutput>status(true)
            .withResult(responseBuilder.build())
            .build();
        // return success
        return Futures.immediateFuture(rpcResult);
    }

    //1610 vf-module-topology-operation
    @Override
    public Future<RpcResult<VfModuleTopologyOperationOutput>> vfModuleTopologyOperation(
        VfModuleTopologyOperationInput input) {

        final String svcOperation = "vf-module-topology-operation";
        VfModuleServiceData vfModuleServiceData;
        ServiceStatusBuilder serviceStatusBuilder = new ServiceStatusBuilder();
        Properties parms = new Properties();

        log.info(svcOperation + CALLED_STR);
        // create a new response object
        VfModuleTopologyOperationOutputBuilder responseBuilder = new VfModuleTopologyOperationOutputBuilder();

        // Validate vf-module-id from vf-module-request-information
        if (input == null || input.getVfModuleRequestInformation() == null
            || input.getVfModuleRequestInformation().getVfModuleId() == null) {
            log.debug(EXITING_STR + svcOperation + " because of invalid input, null or empty vf-module-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage(INVALID_INPUT_VF_MODULE_STR);
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<VfModuleTopologyOperationOutput> rpcResult = RpcResultBuilder
                .<VfModuleTopologyOperationOutput>status(true)
                .withResult(responseBuilder.build())
                .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Grab the vf-module-request-information.vf-module-id from the input buffer
        String vfid = input.getVfModuleRequestInformation().getVfModuleId();
        String preloadName = input.getVfModuleRequestInformation().getVfModuleName();
        String preloadType = input.getVfModuleRequestInformation().getVfModuleModelId();

        // Make sure we have a valid siid
        if (vfid == null || vfid.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid vf-module-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage(INVALID_INPUT_VF_MODULE_STR);
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<VfModuleTopologyOperationOutput> rpcResult = RpcResultBuilder
                .<VfModuleTopologyOperationOutput>status(true)
                .withResult(responseBuilder.build())
                .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // 1610 add vf-module-id to vnf-instance-list.vf-module-relationship-list
        String viid = input.getVfModuleRequestInformation().getVnfInstanceId();

        if (viid == null || viid.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid vnf-instance-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage(INVALID_INPUT_VNF_INSTANCE_STR);
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<VfModuleTopologyOperationOutput> rpcResult = RpcResultBuilder
                .<VfModuleTopologyOperationOutput>status(true)
                .withResult(responseBuilder.build())
                .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        if (input.getSdncRequestHeader() != null) {
            responseBuilder.setSvcRequestId(input.getSdncRequestHeader().getSvcRequestId());
            setRequestIdAsMDC(input.getSdncRequestHeader().getSvcRequestId());
        }

        // Get vf-module-preload-data
        VfModulePreloadDataBuilder vfModulePreloadDataBuilder = new VfModulePreloadDataBuilder();
        getVfModulePreloadData(preloadName, preloadType, vfModulePreloadDataBuilder);

        // Get vf-module-service-data
        VfModuleServiceDataBuilder vfModuleServiceDataBuilder = new VfModuleServiceDataBuilder();
        getVfModuleServiceData(vfid, vfModuleServiceDataBuilder);

        // Get vf-module operation-data
        VfModuleServiceDataBuilder operDataBuilder = new VfModuleServiceDataBuilder();
        getVfModuleServiceData(vfid, operDataBuilder, LogicalDatastoreType.OPERATIONAL);

        // 1610 Need to pull vnf-instance-list.vf-module-relationship-list from MD-SAL
        VnfInstanceServiceDataBuilder vnfInstanceServiceDataBuilder = new VnfInstanceServiceDataBuilder();
        getVnfInstanceServiceData(viid, vnfInstanceServiceDataBuilder);

        // vnf-instance operational-data
        VnfInstanceServiceDataBuilder vnfInstanceOperDataBuilder = new VnfInstanceServiceDataBuilder();
        getVnfInstanceServiceData(viid, vnfInstanceOperDataBuilder, LogicalDatastoreType.OPERATIONAL);

        // Set the serviceStatus based on input
        setServiceStatus(serviceStatusBuilder, input.getSdncRequestHeader());
        setServiceStatus(serviceStatusBuilder, input.getRequestInformation());

        //
        // setup a service-data object builder
        // ACTION vnf-topology-operation
        // INPUT:
        //  USES request-information;
        //  USES service-information;
        //  USES vnf-request-information
        // OUTPUT:
        //  USES vnf-information
        //  USES service-information
        //
        // container service-data

        log.info(ADDING_INPUT_DATA_STR + svcOperation + " [" + vfid + INPUT_STR + input);
        VfModuleTopologyOperationInputBuilder inputBuilder = new VfModuleTopologyOperationInputBuilder(input);
        VnfSdnUtil.toProperties(parms, inputBuilder.build());

        log.info(ADDING_OPERATIONAL_DATA_STR + svcOperation + " [" + vfid + "] vf-module operational-data: "
            + operDataBuilder.build());
        VnfSdnUtil.toProperties(parms, OPERATIONAL_DATA, operDataBuilder);

        log.info(ADDING_CONFIG_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType
            + "] vf-module-preload-data: " + vfModulePreloadDataBuilder.build());
        VnfSdnUtil.toProperties(parms, "vf-module-preload-data", vfModulePreloadDataBuilder);

        log.info("Adding vnf-instance CONFIG data for " + svcOperation + " [" + viid + "] vnf-instance-service-data: "
            + vnfInstanceServiceDataBuilder.build());
        VnfSdnUtil.toProperties(parms, "vnf-instance-service-data", vnfInstanceServiceDataBuilder);

        log.info("Adding vnf-instance OPERATIONAL data for " + svcOperation + " [" + viid
            + "] vnf-instance operational-data: " + vnfInstanceOperDataBuilder.build());
        VnfSdnUtil.toProperties(parms, "vnf-instance-operational-data", vnfInstanceOperDataBuilder);

        // Call SLI sync method
        // Get SvcLogicService reference

        Properties respProps = null;
        String errorCode = "200";
        String errorMessage = null;
        String ackFinal = "Y";

        try {
            if (svcLogicClient.hasGraph(VNF_API, svcOperation, null, "sync")) {

                try {
                    respProps = svcLogicClient
                        .execute(VNF_API, svcOperation, null, "sync", vfModuleServiceDataBuilder, parms);
                } catch (Exception e) {
                    log.error("Caught exception executing service logic on vf-module for " + svcOperation, e);
                    errorMessage = e.getMessage();
                    errorCode = "500";
                }

            } else {
                errorMessage = "No service logic active for VNF-API: '" + svcOperation + "'";
                errorCode = "503";
            }
        } catch (Exception e) {
            errorCode = "500";
            errorMessage = e.getMessage();
            log.error("Caught exception looking for service logic", e);
        }

        if (respProps != null) {
            errorCode = respProps.getProperty("error-code");
            errorMessage = respProps.getProperty("error-message");
            ackFinal = respProps.getProperty("ack-final", "Y");
        }

        setServiceStatus(serviceStatusBuilder, errorCode, errorMessage, ackFinal);
        serviceStatusBuilder.setRequestStatus(RequestStatus.Synccomplete);
        serviceStatusBuilder.setRpcName(RpcName.VfModuleTopologyOperation);

        if (errorCode != null && errorCode.length() != 0 && !("0".equals(errorCode) || "200".equals(errorCode))) {
            responseBuilder.setResponseCode(errorCode);
            responseBuilder.setResponseMessage(errorMessage);
            responseBuilder.setAckFinalIndicator(ackFinal);
            VfModuleListBuilder vfModuleListBuilder = new VfModuleListBuilder();
            vfModuleListBuilder.setVfModuleId(vfid);
            vfModuleListBuilder.setServiceStatus(serviceStatusBuilder.build());
            try {
                saveVfModuleList(vfModuleListBuilder.build(), true, LogicalDatastoreType.CONFIGURATION);
            } catch (Exception e) {
                log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + vfid + "] \n", e);
            }
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + vfid + "] " + responseBuilder.build());
            RpcResult<VfModuleTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<VfModuleTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Got success from SLI
        // save vf-module-service-data in MD-SAL
        try {
            vfModuleServiceData = vfModuleServiceDataBuilder.build();
            log.info(
                UPDATING_MD_SAL_STR + svcOperation + " [" + vfid + "] VfModuleServiceData: " + vfModuleServiceData);
            // vf-module-list
            VfModuleListBuilder vfModuleListBuilder = new VfModuleListBuilder();
            vfModuleListBuilder.setVfModuleServiceData(vfModuleServiceData);
            vfModuleListBuilder.setVfModuleId(vfModuleServiceData.getVfModuleId());
            vfModuleListBuilder.setServiceStatus(serviceStatusBuilder.build());
            saveVfModuleList(vfModuleListBuilder.build(), false, LogicalDatastoreType.CONFIGURATION);
            if (input.getSdncRequestHeader() != null && input.getSdncRequestHeader().getSvcAction() != null) {
                // Only update operational tree on Delete or Activate
                if (input.getSdncRequestHeader().getSvcAction().equals(SvcAction.Delete) || input.getSdncRequestHeader()
                    .getSvcAction().equals(SvcAction.Activate)) {
                    log.info(UPDATING_OPERATIONAL_TREE_STR);
                    saveVfModuleList(vfModuleListBuilder.build(), false, LogicalDatastoreType.OPERATIONAL);
                }
            }
            VfModuleInformationBuilder vfModuleInformationBuilder = new VfModuleInformationBuilder();
            vfModuleInformationBuilder.setVfModuleId(vfid);
            responseBuilder.setVfModuleInformation(vfModuleInformationBuilder.build());
            responseBuilder.setServiceInformation(vfModuleServiceData.getServiceInformation());
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + vfid + "] \n", e);
            responseBuilder.setResponseCode("500");
            responseBuilder.setResponseMessage(e.toString());
            responseBuilder.setAckFinalIndicator("Y");
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + vfid + "] " + responseBuilder.build());
            RpcResult<VfModuleTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<VfModuleTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Update succeeded
        responseBuilder.setResponseCode(errorCode);
        responseBuilder.setAckFinalIndicator(ackFinal);
        if (errorMessage != null) {
            responseBuilder.setResponseMessage(errorMessage);
        }
        log.info("Updated vf-module in MD-SAL for " + svcOperation + " [" + vfid + "] ");
        log.info(RETURNED_SUCCESS_STR + svcOperation + " [" + vfid + "] " + responseBuilder.build());

        RpcResult<VfModuleTopologyOperationOutput> rpcResult =
            RpcResultBuilder.<VfModuleTopologyOperationOutput>status(true).withResult(responseBuilder.build()).build();
        // return success
        return Futures.immediateFuture(rpcResult);
    }

    @Override
    public Future<RpcResult<VnfTopologyOperationOutput>> vnfTopologyOperation(VnfTopologyOperationInput input) {
        final String svcOperation = "vnf-topology-operation";
        ServiceData serviceData;
        ServiceStatusBuilder serviceStatusBuilder = new ServiceStatusBuilder();
        Properties parms = new Properties();

        log.info(svcOperation + CALLED_STR);
        // create a new response object
        VnfTopologyOperationOutputBuilder responseBuilder = new VnfTopologyOperationOutputBuilder();

        if (input == null || input.getServiceInformation() == null
            || input.getServiceInformation().getServiceInstanceId() == null
            || input.getServiceInformation().getServiceInstanceId().length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid input, null or empty service-instance-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, null or empty service-instance-id");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<VnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<VnfTopologyOperationOutput>status(true).withResult(responseBuilder.build()).build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        if (input.getVnfRequestInformation() == null || input.getVnfRequestInformation().getVnfId() == null
            || input.getVnfRequestInformation().getVnfId().length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid input, null or empty vf-module-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, null or empty vf-module-id");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<VnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<VnfTopologyOperationOutput>status(true).withResult(responseBuilder.build()).build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Grab the service instance ID from the input buffer
        String siid = input.getVnfRequestInformation().getVnfId();
        String preloadName = input.getVnfRequestInformation().getVnfName();
        String preloadType = input.getVnfRequestInformation().getVnfType();

        if (input.getSdncRequestHeader() != null) {
            responseBuilder.setSvcRequestId(input.getSdncRequestHeader().getSvcRequestId());
            setRequestIdAsMDC(input.getSdncRequestHeader().getSvcRequestId());
        }

        PreloadDataBuilder preloadDataBuilder = new PreloadDataBuilder();
        getPreloadData(preloadName, preloadType, preloadDataBuilder);

        ServiceDataBuilder serviceDataBuilder = new ServiceDataBuilder();
        getServiceData(siid, serviceDataBuilder);

        ServiceDataBuilder operDataBuilder = new ServiceDataBuilder();
        getServiceData(siid, operDataBuilder, LogicalDatastoreType.OPERATIONAL);

        // Set the serviceStatus based on input
        setServiceStatus(serviceStatusBuilder, input.getSdncRequestHeader());
        setServiceStatus(serviceStatusBuilder, input.getRequestInformation());

        //
        // setup a service-data object builder
        // ACTION vnf-topology-operation
        // INPUT:
        //  USES request-information;
        //  USES vnf-request-information
        // OUTPUT:
        //  USES vnf-information
        //  USES service-information
        //
        // container service-data
        //   uses oper-status;

        log.info(ADDING_INPUT_DATA_STR + svcOperation + " [" + siid + INPUT_STR + input);
        VnfTopologyOperationInputBuilder inputBuilder = new VnfTopologyOperationInputBuilder(input);
        VnfSdnUtil.toProperties(parms, inputBuilder.build());

        log.info(ADDING_OPERATIONAL_DATA_STR + svcOperation + " [" + siid + OPERATIONAL_DATA_STR + operDataBuilder
            .build());
        VnfSdnUtil.toProperties(parms, OPERATIONAL_DATA, operDataBuilder);

        log.info(
            "Adding CONFIG data for " + svcOperation + " [" + preloadName + "," + preloadType + "] preload-data: "
                + preloadDataBuilder.build());
        VnfSdnUtil.toProperties(parms, "preload-data", preloadDataBuilder);

        // Call SLI sync method
        // Get SvcLogicService reference
        Properties respProps = null;
        String errorCode = "200";
        String errorMessage = null;
        String ackFinal = "Y";

        try {
            if (svcLogicClient.hasGraph(VNF_API, svcOperation, null, "sync")) {

                try {
                    respProps =
                        svcLogicClient.execute(VNF_API, svcOperation, null, "sync", serviceDataBuilder, parms);
                } catch (Exception e) {
                    log.error("Caught exception executing service logic for " + svcOperation, e);
                    errorMessage = e.getMessage();
                    errorCode = "500";
                }
            } else {
                errorMessage = "No service logic active for VNF-API: '" + svcOperation + "'";
                errorCode = "503";
            }
        } catch (Exception e) {
            errorCode = "500";
            errorMessage = e.getMessage();
            log.error("Caught exception looking for service logic", e);
        }

        if (respProps != null) {
            errorCode = respProps.getProperty("error-code");
            errorMessage = respProps.getProperty("error-message");
            ackFinal = respProps.getProperty("ack-final", "Y");
        }

        setServiceStatus(serviceStatusBuilder, errorCode, errorMessage, ackFinal);
        serviceStatusBuilder.setRequestStatus(RequestStatus.Synccomplete);
        serviceStatusBuilder.setRpcName(RpcName.VnfTopologyOperation);

        if (errorCode != null && errorCode.length() != 0 && !("0".equals(errorCode) || "200".equals(errorCode))) {
            responseBuilder.setResponseCode(errorCode);
            responseBuilder.setResponseMessage(errorMessage);
            responseBuilder.setAckFinalIndicator(ackFinal);
            VnfListBuilder vnfListBuilder = new VnfListBuilder();
            vnfListBuilder.setVnfId(siid);
            vnfListBuilder.setServiceStatus(serviceStatusBuilder.build());
            try {
                saveVnfList(vnfListBuilder.build(), true, LogicalDatastoreType.CONFIGURATION);
            } catch (Exception e) {
                log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + siid + "] \n", e);
            }
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + siid + "] " + responseBuilder.build());
            RpcResult<VnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<VnfTopologyOperationOutput>status(true).withResult(responseBuilder.build()).build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Got success from SLI
        try {
            serviceData = serviceDataBuilder.build();
            log.info(UPDATING_MD_SAL_STR + svcOperation + " [" + siid + "] ServiceData: " + serviceData);
            // svc-configuration-list
            VnfListBuilder vnfListBuilder = new VnfListBuilder();
            vnfListBuilder.setServiceData(serviceData);
            vnfListBuilder.setVnfId(serviceData.getVnfId());
            siid = serviceData.getVnfId();
            vnfListBuilder.setServiceStatus(serviceStatusBuilder.build());
            saveVnfList(vnfListBuilder.build(), false, LogicalDatastoreType.CONFIGURATION);
            if (input.getSdncRequestHeader() != null && input.getSdncRequestHeader().getSvcAction() != null) {
                // Only update operational tree on Delete or Activate
                if (input.getSdncRequestHeader().getSvcAction().equals(SvcAction.Activate)) {
                    log.info(UPDATING_OPERATIONAL_TREE_STR);
                    saveVnfList(vnfListBuilder.build(), false, LogicalDatastoreType.OPERATIONAL);
                } else if (input.getSdncRequestHeader().getSvcAction().equals(SvcAction.Delete) || input
                    .getSdncRequestHeader().getSvcAction().equals(SvcAction.Rollback)) {
                    log.info("Delete OPERATIONAL tree.");
                    deleteVnfList(vnfListBuilder.build(), LogicalDatastoreType.CONFIGURATION);
                    deleteVnfList(vnfListBuilder.build(), LogicalDatastoreType.OPERATIONAL);
                }
            }
            VnfInformationBuilder vnfInformationBuilder = new VnfInformationBuilder();
            vnfInformationBuilder.setVnfId(siid);
            responseBuilder.setVnfInformation(vnfInformationBuilder.build());
            responseBuilder.setServiceInformation(serviceData.getServiceInformation());
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + siid + "] \n", e);
            responseBuilder.setResponseCode("500");
            responseBuilder.setResponseMessage(e.toString());
            responseBuilder.setAckFinalIndicator("Y");
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + siid + "] " + responseBuilder.build());
            RpcResult<VnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<VnfTopologyOperationOutput>status(true).withResult(responseBuilder.build()).build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Update succeeded
        responseBuilder.setResponseCode(errorCode);
        responseBuilder.setAckFinalIndicator(ackFinal);
        if (errorMessage != null) {
            responseBuilder.setResponseMessage(errorMessage);
        }
        log.info(UPDATED_MD_SAL_STR + svcOperation + " [" + siid + "] ");
        log.info(RETURNED_SUCCESS_STR + svcOperation + " [" + siid + "] " + responseBuilder.build());

        RpcResult<VnfTopologyOperationOutput> rpcResult =
            RpcResultBuilder.<VnfTopologyOperationOutput>status(true).withResult(responseBuilder.build()).build();
        // return success
        return Futures.immediateFuture(rpcResult);
    }

    @Override
    public Future<RpcResult<NetworkTopologyOperationOutput>> networkTopologyOperation(
        NetworkTopologyOperationInput input) {

        final String svcOperation = "network-topology-operation";
        ServiceData serviceData;
        ServiceStatusBuilder serviceStatusBuilder = new ServiceStatusBuilder();
        Properties parms = new Properties();

        log.info(svcOperation + CALLED_STR);
        // create a new response object
        NetworkTopologyOperationOutputBuilder responseBuilder = new NetworkTopologyOperationOutputBuilder();

        if (input == null || input.getServiceInformation() == null
            || input.getServiceInformation().getServiceInstanceId() == null
            || input.getServiceInformation().getServiceInstanceId().length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid input, null or empty service-instance-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, null or empty service-instance-id");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<NetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<NetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        if (input.getNetworkRequestInformation() == null
            || input.getNetworkRequestInformation().getNetworkName() == null) {
            log.debug(EXITING_STR + svcOperation + " because of invalid input, null or empty service-instance-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, null or empty service-instance-id");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<NetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<NetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Grab the service instance ID from the input buffer
        String siid;
        if (input.getSdncRequestHeader().getSvcAction().equals(SvcAction.Assign)) {
            siid = input.getNetworkRequestInformation().getNetworkName();
        } else {
            siid = input.getNetworkRequestInformation().getNetworkId();
        }
        String preloadName = input.getNetworkRequestInformation().getNetworkName();
        String preloadType = input.getNetworkRequestInformation().getNetworkType();

        if (input.getSdncRequestHeader() != null) {
            responseBuilder.setSvcRequestId(input.getSdncRequestHeader().getSvcRequestId());
            setRequestIdAsMDC(input.getSdncRequestHeader().getSvcRequestId());
        }

        PreloadDataBuilder preloadDataBuilder = new PreloadDataBuilder();
        getPreloadData(preloadName, preloadType, preloadDataBuilder);

        log.info(ADDING_INPUT_DATA_STR + svcOperation + " [" + siid + INPUT_STR + input);
        NetworkTopologyOperationInputBuilder inputBuilder = new NetworkTopologyOperationInputBuilder(input);
        VnfSdnUtil.toProperties(parms, inputBuilder.build());

        // Call SLI sync method
        // Get SvcLogicService reference
        Properties respProps = null;
        String errorCode = "200";
        String errorMessage = null;
        String ackFinal = "Y";
        String networkId = "error";

        try {
            if (svcLogicClient.hasGraph(VNF_API, svcOperation, null, "sync")) {

                try {
                    respProps =
                        svcLogicClient.execute(VNF_API, svcOperation, null, "sync", preloadDataBuilder, parms);
                } catch (Exception e) {
                    log.error("Caught exception executing service logic for " + svcOperation, e);
                    errorMessage = e.getMessage();
                    errorCode = "500";
                }
            } else {
                errorMessage = "No service logic active for VNF-API: '" + svcOperation + "'";
                errorCode = "503";
            }
        } catch (Exception e) {
            errorCode = "500";
            errorMessage = e.getMessage();
            log.error("Caught exception looking for service logic", e);
        }

        if (respProps != null) {
            errorCode = respProps.getProperty("error-code");
            errorMessage = respProps.getProperty("error-message");
            ackFinal = respProps.getProperty("ack-final", "Y");
            networkId = respProps.getProperty("networkId", "0");
        }

        if (errorCode != null && errorCode.length() != 0 && !("0".equals(errorCode) || "200".equals(errorCode))) {
            responseBuilder.setResponseCode(errorCode);
            responseBuilder.setResponseMessage(errorMessage);
            responseBuilder.setAckFinalIndicator(ackFinal);

            log.error(RETURNED_FAILED_STR + svcOperation + " [" + siid + "] " + responseBuilder.build());

            RpcResult<NetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<NetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Got success from SLI
        try {
            NetworkInformationBuilder networkInformationBuilder = new NetworkInformationBuilder();
            networkInformationBuilder.setNetworkId(networkId);
            responseBuilder.setNetworkInformation(networkInformationBuilder.build());
            responseBuilder.setServiceInformation(input.getServiceInformation());
        } catch (IllegalStateException e) {
            log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + siid + "] \n", e);
            responseBuilder.setResponseCode("500");
            responseBuilder.setResponseMessage(e.toString());
            responseBuilder.setAckFinalIndicator("Y");
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + siid + "] " + responseBuilder.build());
            RpcResult<NetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<NetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            // return error
            return Futures.immediateFuture(rpcResult);
        }

        // Update succeeded
        responseBuilder.setResponseCode(errorCode);
        responseBuilder.setAckFinalIndicator(ackFinal);
        if (errorMessage != null) {
            responseBuilder.setResponseMessage(errorMessage);
        }
        log.info(UPDATED_MD_SAL_STR + svcOperation + " [" + siid + "] ");
        log.info(RETURNED_SUCCESS_STR + svcOperation + " [" + siid + "] " + responseBuilder.build());

        RpcResult<NetworkTopologyOperationOutput> rpcResult =
            RpcResultBuilder.<NetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build()).build();
        // return success
        return Futures.immediateFuture(rpcResult);
    }

    @Override
    public Future<RpcResult<PreloadVnfTopologyOperationOutput>> preloadVnfTopologyOperation(
        PreloadVnfTopologyOperationInput input) {

        final String svcOperation = "preload-vnf-topology-operation";
        PreloadData preloadData;
        Properties parms = new Properties();

        log.info(svcOperation + CALLED_STR);
        // create a new response object
        PreloadVnfTopologyOperationOutputBuilder responseBuilder = new PreloadVnfTopologyOperationOutputBuilder();

        // Result from savePreloadData

        if (input == null || input.getVnfTopologyInformation() == null
            || input.getVnfTopologyInformation().getVnfTopologyIdentifier() == null
            || input.getVnfTopologyInformation().getVnfTopologyIdentifier().getVnfName() == null
            || input.getVnfTopologyInformation().getVnfTopologyIdentifier().getVnfType() == null) {
            log.debug(EXITING_STR + svcOperation + " because of invalid input, null or empty vnf-name or vnf-type");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, null or empty vnf-name or vnf-type");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }

        // Grab the name and type from the input buffer
        String preloadName = input.getVnfTopologyInformation().getVnfTopologyIdentifier().getVnfName();
        String preloadType = input.getVnfTopologyInformation().getVnfTopologyIdentifier().getVnfType();

        // Make sure we have a preload_name and preload_type
        if (preloadName == null || preloadName.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid preload-name");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, invalid preload-name");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }
        if (preloadType == null || preloadType.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid preload-type");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, invalid preload-type");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }

        if (input.getSdncRequestHeader() != null) {
            responseBuilder.setSvcRequestId(input.getSdncRequestHeader().getSvcRequestId());
            setRequestIdAsMDC(input.getSdncRequestHeader().getSvcRequestId());
        }

        PreloadDataBuilder preloadDataBuilder = new PreloadDataBuilder();
        getPreloadData(preloadName, preloadType, preloadDataBuilder);
        PreloadDataBuilder operDataBuilder = new PreloadDataBuilder();
        getPreloadData(preloadName, preloadType, operDataBuilder, LogicalDatastoreType.OPERATIONAL);

        //
        // setup a preload-data object builder
        // ACTION vnf-topology-operation
        // INPUT:
        //  USES request-information;
        //  uses vnf-topology-information;
        // OUTPUT:
        //
        // container preload-data
        log.info(
            ADDING_INPUT_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType + INPUT_STR + input);
        PreloadVnfTopologyOperationInputBuilder inputBuilder = new PreloadVnfTopologyOperationInputBuilder(input);
        VnfSdnUtil.toProperties(parms, inputBuilder.build());
        log.info(ADDING_OPERATIONAL_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType
            + OPERATIONAL_DATA_STR + operDataBuilder.build());
        VnfSdnUtil.toProperties(parms, OPERATIONAL_DATA, operDataBuilder);

        // Call SLI sync method
        // Get SvcLogicService reference
        Properties respProps = null;
        String errorCode = "200";
        String errorMessage = null;
        String ackFinal = "Y";

        try {
            if (svcLogicClient.hasGraph(VNF_API, svcOperation, null, "sync")) {

                try {
                    respProps =
                        svcLogicClient.execute(VNF_API, svcOperation, null, "sync", preloadDataBuilder, parms);
                } catch (Exception e) {
                    log.error("Caught exception executing service logic for " + svcOperation, e);
                    errorMessage = e.getMessage();
                    errorCode = "500";
                }
            } else {
                errorMessage = "No service logic active for VNF-API: '" + svcOperation + "'";
                errorCode = "503";
            }
        } catch (Exception e) {
            errorCode = "500";
            errorMessage = e.getMessage();
            log.error("Caught exception looking for service logic", e);
        }

        if (respProps != null) {
            errorCode = respProps.getProperty("error-code");
            errorMessage = respProps.getProperty("error-message");
            ackFinal = respProps.getProperty("ack-final", "Y");
        }

        if (errorCode != null && errorCode.length() != 0 && !("0".equals(errorCode) || "200".equals(errorCode))) {

            responseBuilder.setResponseCode(errorCode);
            responseBuilder.setResponseMessage(errorMessage);
            responseBuilder.setAckFinalIndicator(ackFinal);

            VnfPreloadListBuilder preloadVnfListBuilder = new VnfPreloadListBuilder();
            preloadVnfListBuilder.setVnfName(preloadName);
            preloadVnfListBuilder.setVnfType(preloadType);
            preloadVnfListBuilder.setPreloadData(preloadDataBuilder.build());
            log.error(
                RETURNED_FAILED_STR + svcOperation + " [" + preloadName + "," + preloadType + "] error code: '"
                    + errorCode + "', Reason: '" + errorMessage + "'");
            try {
                savePreloadList(preloadVnfListBuilder.build(), true, LogicalDatastoreType.CONFIGURATION);
            } catch (Exception e) {
                log.error(
                    CAUGHT_EXCEPTION_STR + svcOperation + " [" + preloadName + "," + preloadType
                        + "] \n", e);
            }
            log.debug("Sending Success rpc result due to external error");
            RpcResult<PreloadVnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }

        // Got success from SLI
        try {
            preloadData = preloadDataBuilder.build();
            log.info(
                UPDATING_MD_SAL_STR + svcOperation + " [" + preloadName + "," + preloadType + "] preloadData: "
                    + preloadData);
            // svc-configuration-list
            VnfPreloadListBuilder preloadVnfListBuilder = new VnfPreloadListBuilder();
            preloadVnfListBuilder.setVnfName(preloadName);
            preloadVnfListBuilder.setVnfType(preloadType);
            preloadVnfListBuilder.setPreloadData(preloadData);

            // SDNGC-989 set merge flag to false
            savePreloadList(preloadVnfListBuilder.build(), false, LogicalDatastoreType.CONFIGURATION);
            log.info(UPDATING_OPERATIONAL_TREE_STR);
            savePreloadList(preloadVnfListBuilder.build(), false, LogicalDatastoreType.OPERATIONAL);
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + preloadName + "," + preloadType
                + "] \n", e);
            responseBuilder.setResponseCode("500");
            responseBuilder.setResponseMessage(e.toString());
            responseBuilder.setAckFinalIndicator("Y");
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + preloadName + "," + preloadType + "] "
                + responseBuilder.build());
            RpcResult<PreloadVnfTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfTopologyOperationOutput>status(false).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }

        // Update succeeded
        responseBuilder.setResponseCode(errorCode);
        responseBuilder.setAckFinalIndicator(ackFinal);
        if (errorMessage != null) {
            responseBuilder.setResponseMessage(errorMessage);
        }
        log.info(UPDATED_MD_SAL_STR + svcOperation + " [" + preloadName + "," + preloadType + "] ");
        log.info(
            RETURNED_SUCCESS_STR + svcOperation + " [" + preloadName + "," + preloadType + "] " + responseBuilder
                .build());

        RpcResult<PreloadVnfTopologyOperationOutput> rpcResult =
            RpcResultBuilder.<PreloadVnfTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                .build();
        return Futures.immediateFuture(rpcResult);
    }

    //1610 preload-vnf-instance-topology-operation
    @Override
    public Future<RpcResult<PreloadVnfInstanceTopologyOperationOutput>> preloadVnfInstanceTopologyOperation(
        PreloadVnfInstanceTopologyOperationInput input) {

        final String svcOperation = "preload-vnf-instance-topology-operation";
        VnfInstancePreloadData vnfInstancePreloadData;
        Properties parms = new Properties();

        log.info(svcOperation + CALLED_STR);
        // create a new response object
        PreloadVnfInstanceTopologyOperationOutputBuilder responseBuilder =
            new PreloadVnfInstanceTopologyOperationOutputBuilder();

        if (input == null || input.getVnfInstanceTopologyInformation() == null
            || input.getVnfInstanceTopologyInformation().getVnfInstanceIdentifiers().getVnfInstanceName() == null
            || input.getVnfInstanceTopologyInformation().getVnfInstanceIdentifiers().getVnfModelId() == null) {
            log.debug(EXITING_STR + svcOperation
                + " because of invalid input, null or empty vnf-instance-name or vnf-model-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, null or empty vnf-instance-name or vnf-model-id");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVnfInstanceTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfInstanceTopologyOperationOutput>status(true)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        // Grab the name and type from the input buffer
        String preloadName =
            input.getVnfInstanceTopologyInformation().getVnfInstanceIdentifiers().getVnfInstanceName();
        String preloadType = input.getVnfInstanceTopologyInformation().getVnfInstanceIdentifiers().getVnfModelId();

        // Make sure we have a preloadName and preloadType
        if (preloadName == null || preloadName.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid preload-name");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, invalid preload-name");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVnfInstanceTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfInstanceTopologyOperationOutput>status(true)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }
        if (preloadType == null || preloadType.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid preload-type");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, invalid preload-type");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVnfInstanceTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfInstanceTopologyOperationOutput>status(true)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        if (input.getSdncRequestHeader() != null) {
            responseBuilder.setSvcRequestId(input.getSdncRequestHeader().getSvcRequestId());
            setRequestIdAsMDC(input.getSdncRequestHeader().getSvcRequestId());
        }

        VnfInstancePreloadDataBuilder vnfInstancePreloadDataBuilder = new VnfInstancePreloadDataBuilder();
        getVnfInstancePreloadData(preloadName, preloadType, vnfInstancePreloadDataBuilder);

        VnfInstancePreloadDataBuilder operDataBuilder = new VnfInstancePreloadDataBuilder();
        getVnfInstancePreloadData(preloadName, preloadType, operDataBuilder, LogicalDatastoreType.OPERATIONAL);

        //
        // setup a preload-data object builder
        // ACTION vnf-topology-operation
        // INPUT:
        //  uses vnf-topology-information;
        // OUTPUT:
        // container preload-data
        log.info(
            ADDING_CONFIG_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType + INPUT_STR + input);
        PreloadVnfInstanceTopologyOperationInputBuilder inputBuilder =
            new PreloadVnfInstanceTopologyOperationInputBuilder(input);
        VnfSdnUtil.toProperties(parms, inputBuilder.build());
        log.info(ADDING_OPERATIONAL_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType
            + OPERATIONAL_DATA_STR + operDataBuilder.build());
        VnfSdnUtil.toProperties(parms, OPERATIONAL_DATA, operDataBuilder);

        // Call SLI sync method
        // Get SvcLogicService reference
        Properties respProps = null;
        String errorCode = "200";
        String errorMessage = null;
        String ackFinal = "Y";

        try {
            if (svcLogicClient.hasGraph(VNF_API, svcOperation, null, "sync")) {

                try {
                    respProps = svcLogicClient
                        .execute(VNF_API, svcOperation, null, "sync", vnfInstancePreloadDataBuilder, parms);
                } catch (Exception e) {
                    log.error("Caught exception executing service logic for " + svcOperation, e);
                    errorMessage = e.getMessage();
                    errorCode = "500";
                }
            } else {
                errorMessage = "No service logic active for VNF-API: '" + svcOperation + "'";
                errorCode = "503";
            }
        } catch (Exception e) {
            errorCode = "500";
            errorMessage = e.getMessage();
            log.error("Caught exception looking for service logic", e);
        }

        if (respProps != null) {
            errorCode = respProps.getProperty("error-code");
            errorMessage = respProps.getProperty("error-message");
            ackFinal = respProps.getProperty("ack-final", "Y");
        }

        if (errorCode != null && errorCode.length() != 0 && !("0".equals(errorCode) || "200".equals(errorCode))) {

            responseBuilder.setResponseCode(errorCode);
            responseBuilder.setResponseMessage(errorMessage);
            responseBuilder.setAckFinalIndicator(ackFinal);

            VnfInstancePreloadListBuilder vnfInstancePreloadListBuilder = new VnfInstancePreloadListBuilder();
            vnfInstancePreloadListBuilder.setVnfInstanceName(preloadName);
            vnfInstancePreloadListBuilder.setVnfModelId(preloadType);
            vnfInstancePreloadListBuilder.setVnfInstancePreloadData(vnfInstancePreloadDataBuilder.build());
            log.error(
                RETURNED_FAILED_STR + svcOperation + " [" + preloadName + "," + preloadType + "] error code: '"
                    + errorCode + "', Reason: '" + errorMessage + "'");
            try {
                saveVnfInstancePreloadList(vnfInstancePreloadListBuilder.build(), true,
                    LogicalDatastoreType.CONFIGURATION);
            } catch (Exception e) {
                log.error(
                    CAUGHT_EXCEPTION_STR + svcOperation + " [" + preloadName + "," + preloadType
                        + "] \n", e);
            }
            log.debug("Sending Success rpc result due to external error");
            RpcResult<PreloadVnfInstanceTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfInstanceTopologyOperationOutput>status(true)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        // Got success from SLI
        try {
            vnfInstancePreloadData = vnfInstancePreloadDataBuilder.build();
            log.info(
                UPDATING_MD_SAL_STR + svcOperation + " [" + preloadName + "," + preloadType + "] preloadData: "
                    + vnfInstancePreloadData);
            // svc-configuration-list
            VnfInstancePreloadListBuilder vnfInstancePreloadListBuilder = new VnfInstancePreloadListBuilder();
            vnfInstancePreloadListBuilder.setVnfInstanceName(preloadName);
            vnfInstancePreloadListBuilder.setVnfModelId(preloadType);
            vnfInstancePreloadListBuilder.setVnfInstancePreloadData(vnfInstancePreloadData);

            // SDNGC-989 set merge flag to false
            saveVnfInstancePreloadList(vnfInstancePreloadListBuilder.build(), false,
                LogicalDatastoreType.CONFIGURATION);
            log.info(UPDATING_OPERATIONAL_TREE_STR);
            saveVnfInstancePreloadList(vnfInstancePreloadListBuilder.build(), false, LogicalDatastoreType.OPERATIONAL);
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + preloadName + "," + preloadType
                + "] \n", e);
            responseBuilder.setResponseCode("500");
            responseBuilder.setResponseMessage(e.toString());
            responseBuilder.setAckFinalIndicator("Y");
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + preloadName + "," + preloadType + "] "
                + responseBuilder.build());
            RpcResult<PreloadVnfInstanceTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVnfInstanceTopologyOperationOutput>status(false)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        // Update succeeded
        responseBuilder.setResponseCode(errorCode);
        responseBuilder.setAckFinalIndicator(ackFinal);
        if (errorMessage != null) {
            responseBuilder.setResponseMessage(errorMessage);
        }
        log.info(UPDATED_MD_SAL_STR + svcOperation + " [" + preloadName + "," + preloadType + "] ");
        log.info(
            RETURNED_SUCCESS_STR + svcOperation + " [" + preloadName + "," + preloadType + "] " + responseBuilder
                .build());

        RpcResult<PreloadVnfInstanceTopologyOperationOutput> rpcResult =
            RpcResultBuilder.<PreloadVnfInstanceTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                .build();
        return Futures.immediateFuture(rpcResult);
    }


    //1610 preload-vf-module-topology-operation
    @Override
    public Future<RpcResult<PreloadVfModuleTopologyOperationOutput>> preloadVfModuleTopologyOperation(
        PreloadVfModuleTopologyOperationInput input) {

        final String svcOperation = "preload-vf-module-topology-operation";
        VfModulePreloadData vfModulePreloadData;
        Properties parms = new Properties();

        log.info(svcOperation + CALLED_STR);
        // create a new response object
        PreloadVfModuleTopologyOperationOutputBuilder responseBuilder =
            new PreloadVfModuleTopologyOperationOutputBuilder();

        // Result from savePreloadData

        if (input == null || input.getVfModuleTopologyInformation() == null
            || input.getVfModuleTopologyInformation().getVfModuleIdentifiers().getVfModuleName() == null
            || input.getVfModuleTopologyInformation().getVfModuleIdentifiers().getVfModuleModelId() == null) {
            log.debug(EXITING_STR + svcOperation
                + " because of invalid input, null or empty vf-module-name or vf-module-model-id");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, null or empty vf-module-name or vf-module-model-id");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVfModuleTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVfModuleTopologyOperationOutput>status(true)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        // Grab the name and type from the input buffer
        String preloadName = input.getVfModuleTopologyInformation().getVfModuleIdentifiers().getVfModuleName();
        String preloadType = input.getVfModuleTopologyInformation().getVfModuleIdentifiers().getVfModuleModelId();

        // Make sure we have a preloadName and preloadType
        if (preloadName == null || preloadName.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid preload-name");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, invalid preload-name");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVfModuleTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVfModuleTopologyOperationOutput>status(true)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }
        if (preloadType == null || preloadType.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid preload-type");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("invalid input, invalid preload-type");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadVfModuleTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVfModuleTopologyOperationOutput>status(true)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        if (input.getSdncRequestHeader() != null) {
            responseBuilder.setSvcRequestId(input.getSdncRequestHeader().getSvcRequestId());
            setRequestIdAsMDC(input.getSdncRequestHeader().getSvcRequestId());
        }

        VfModulePreloadDataBuilder vfModulePreloadDataBuilder = new VfModulePreloadDataBuilder();
        getVfModulePreloadData(preloadName, preloadType, vfModulePreloadDataBuilder);

        VfModulePreloadDataBuilder operDataBuilder = new VfModulePreloadDataBuilder();
        getVfModulePreloadData(preloadName, preloadType, operDataBuilder, LogicalDatastoreType.OPERATIONAL);

        //
        // setup a preload-data object builder
        // ACTION vnf-topology-operation
        // INPUT:
        //  USES request-information;
        //  uses vnf-topology-information;
        // OUTPUT:
        //
        // container preload-data

        log.info(
            ADDING_INPUT_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType + INPUT_STR + input);
        PreloadVfModuleTopologyOperationInputBuilder inputBuilder =
            new PreloadVfModuleTopologyOperationInputBuilder(input);
        VnfSdnUtil.toProperties(parms, inputBuilder.build());
        log.info(ADDING_OPERATIONAL_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType
            + OPERATIONAL_DATA_STR + operDataBuilder.build());
        VnfSdnUtil.toProperties(parms, OPERATIONAL_DATA, operDataBuilder);

        // Call SLI sync method
        // Get SvcLogicService reference
        Properties respProps = null;
        String errorCode = "200";
        String errorMessage = null;
        String ackFinal = "Y";

        try {
            if (svcLogicClient.hasGraph(VNF_API, svcOperation, null, "sync")) {
                try {
                    respProps = svcLogicClient
                        .execute(VNF_API, svcOperation, null, "sync", vfModulePreloadDataBuilder, parms);
                } catch (Exception e) {
                    log.error("Caught exception executing service logic for " + svcOperation, e);
                    errorMessage = e.getMessage();
                    errorCode = "500";
                }
            } else {
                errorMessage = "No service logic active for VNF-API: '" + svcOperation + "'";
                errorCode = "503";
            }
        } catch (Exception e) {
            errorCode = "500";
            errorMessage = e.getMessage();
            log.error("Caught exception looking for service logic", e);
        }

        if (respProps != null) {
            errorCode = respProps.getProperty("error-code");
            errorMessage = respProps.getProperty("error-message");
            ackFinal = respProps.getProperty("ack-final", "Y");
        }

        if (errorCode != null && errorCode.length() != 0 && !("0".equals(errorCode) || "200".equals(errorCode))) {

            responseBuilder.setResponseCode(errorCode);
            responseBuilder.setResponseMessage(errorMessage);
            responseBuilder.setAckFinalIndicator(ackFinal);

            VfModulePreloadListBuilder vfModulePreloadListBuilder = new VfModulePreloadListBuilder();
            vfModulePreloadListBuilder.setVfModuleName(preloadName);
            vfModulePreloadListBuilder.setVfModuleModelId(preloadType);
            vfModulePreloadListBuilder.setVfModulePreloadData(vfModulePreloadDataBuilder.build());
            log.error(
                RETURNED_FAILED_STR + svcOperation + " [" + preloadName + "," + preloadType + "] error code: '"
                    + errorCode + "', Reason: '" + errorMessage + "'");
            try {
                saveVfModulePreloadList(vfModulePreloadListBuilder.build(), true, LogicalDatastoreType.CONFIGURATION);
            } catch (Exception e) {
                log.error(
                    CAUGHT_EXCEPTION_STR + svcOperation + " [" + preloadName + "," + preloadType
                        + "] \n", e);
            }
            log.debug("Sending Success rpc result due to external error");
            RpcResult<PreloadVfModuleTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVfModuleTopologyOperationOutput>status(true)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        // Got success from SLI
        try {
            vfModulePreloadData = vfModulePreloadDataBuilder.build();
            log.info(
                UPDATING_MD_SAL_STR + svcOperation + " [" + preloadName + "," + preloadType + "] preloadData: "
                    + vfModulePreloadData);
            // svc-configuration-list
            VfModulePreloadListBuilder vfModulePreloadListBuilder = new VfModulePreloadListBuilder();
            vfModulePreloadListBuilder.setVfModuleName(preloadName);
            vfModulePreloadListBuilder.setVfModuleModelId(preloadType);
            vfModulePreloadListBuilder.setVfModulePreloadData(vfModulePreloadData);

            // SDNGC-989 set merge flag to false
            saveVfModulePreloadList(vfModulePreloadListBuilder.build(), false, LogicalDatastoreType.CONFIGURATION);
            log.info(UPDATING_OPERATIONAL_TREE_STR);
            saveVfModulePreloadList(vfModulePreloadListBuilder.build(), false, LogicalDatastoreType.OPERATIONAL);
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + preloadName + "," + preloadType
                + "] \n", e);
            responseBuilder.setResponseCode("500");
            responseBuilder.setResponseMessage(e.toString());
            responseBuilder.setAckFinalIndicator("Y");
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + preloadName + "," + preloadType + "] "
                + responseBuilder.build());
            RpcResult<PreloadVfModuleTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadVfModuleTopologyOperationOutput>status(false)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        // Update succeeded
        responseBuilder.setResponseCode(errorCode);
        responseBuilder.setAckFinalIndicator(ackFinal);
        if (errorMessage != null) {
            responseBuilder.setResponseMessage(errorMessage);
        }
        log.info(UPDATED_MD_SAL_STR + svcOperation + " [" + preloadName + "," + preloadType + "] ");
        log.info(
            RETURNED_SUCCESS_STR + svcOperation + " [" + preloadName + "," + preloadType + "] " + responseBuilder
                .build());

        RpcResult<PreloadVfModuleTopologyOperationOutput> rpcResult =
            RpcResultBuilder.<PreloadVfModuleTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                .build();
        return Futures.immediateFuture(rpcResult);
    }


    @Override
    public Future<RpcResult<PreloadNetworkTopologyOperationOutput>> preloadNetworkTopologyOperation(
        PreloadNetworkTopologyOperationInput input) {

        final String svcOperation = "preload-network-topology-operation";
        PreloadData preloadData = null;
        Properties parms = new Properties();

        log.info(svcOperation + CALLED_STR);
        // create a new response object
        PreloadNetworkTopologyOperationOutputBuilder responseBuilder =
            new PreloadNetworkTopologyOperationOutputBuilder();

        // Result from savePreloadData

        if (input == null || input.getNetworkTopologyInformation() == null
            || input.getNetworkTopologyInformation().getNetworkTopologyIdentifier() == null
            || input.getNetworkTopologyInformation().getNetworkTopologyIdentifier().getNetworkName() == null
            || input.getNetworkTopologyInformation().getNetworkTopologyIdentifier().getNetworkType() == null) {
            log.debug(EXITING_STR + svcOperation + " because of invalid input, null or" +
                " empty network-name or network-type");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("input, null or empty network-name or network-type");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadNetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadNetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }

        // Grab the name and type from the input buffer
        String preloadName = input.getNetworkTopologyInformation().getNetworkTopologyIdentifier().getNetworkName();
        String preloadType = input.getNetworkTopologyInformation().getNetworkTopologyIdentifier().getNetworkType();

        // Make sure we have a preloadName and preloadType
        if (preloadName == null || preloadName.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid preload-name");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("input, invalid preload-name");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadNetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadNetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }

        if (preloadType == null || preloadType.length() == 0) {
            log.debug(EXITING_STR + svcOperation + " because of invalid preload-type");
            responseBuilder.setResponseCode("403");
            responseBuilder.setResponseMessage("input, invalid preload-type");
            responseBuilder.setAckFinalIndicator("Y");
            RpcResult<PreloadNetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadNetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }

        if (input.getSdncRequestHeader() != null) {
            responseBuilder.setSvcRequestId(input.getSdncRequestHeader().getSvcRequestId());
            setRequestIdAsMDC(input.getSdncRequestHeader().getSvcRequestId());
        }

        PreloadDataBuilder preloadDataBuilder = new PreloadDataBuilder();
        getPreloadData(preloadName, preloadType, preloadDataBuilder);

        PreloadDataBuilder operDataBuilder = new PreloadDataBuilder();
        getPreloadData(preloadName, preloadType, operDataBuilder, LogicalDatastoreType.OPERATIONAL);

        //
        // setup a preload-data object builder
        // ACTION vnf-topology-operation
        // INPUT:
        //  USES request-information;
        //  uses vnf-topology-information;
        // OUTPUT:
        //
        // container preload-data
        log.info(
            ADDING_INPUT_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType + INPUT_STR + input);
        PreloadNetworkTopologyOperationInputBuilder inputBuilder =
            new PreloadNetworkTopologyOperationInputBuilder(input);
        VnfSdnUtil.toProperties(parms, inputBuilder.build());
        log.info(ADDING_OPERATIONAL_DATA_STR + svcOperation + " [" + preloadName + "," + preloadType
            + OPERATIONAL_DATA_STR + operDataBuilder.build());
        VnfSdnUtil.toProperties(parms, OPERATIONAL_DATA, operDataBuilder);

        // Call SLI sync method
        // Get SvcLogicService reference
        Properties respProps = null;
        String errorCode = "200";
        String errorMessage = null;
        String ackFinal = "Y";

        try {
            if (svcLogicClient.hasGraph(VNF_API, svcOperation, null, "sync")) {
                try {
                    respProps =
                        svcLogicClient.execute(VNF_API, svcOperation, null, "sync", preloadDataBuilder, parms);
                } catch (Exception e) {
                    log.error("Caught exception executing service logic for " + svcOperation, e);
                    errorMessage = e.getMessage();
                    errorCode = "500";
                }
            } else {
                errorMessage = "No service logic active for VNF-API: '" + svcOperation + "'";
                errorCode = "503";
            }
        } catch (Exception e) {
            errorCode = "500";
            errorMessage = e.getMessage();
            log.error("Caught exception looking for service logic", e);
        }

        if (respProps != null) {
            errorCode = respProps.getProperty("error-code");
            errorMessage = respProps.getProperty("error-message");
            ackFinal = respProps.getProperty("ack-final", "Y");
        }

        if (errorCode != null && errorCode.length() != 0 && !("0".equals(errorCode) || "200".equals(errorCode))) {
            responseBuilder.setResponseCode(errorCode);
            responseBuilder.setResponseMessage(errorMessage);
            responseBuilder.setAckFinalIndicator(ackFinal);

            VnfPreloadListBuilder preloadVnfListBuilder = new VnfPreloadListBuilder();
            preloadVnfListBuilder.setVnfName(preloadName);
            preloadVnfListBuilder.setVnfType(preloadType);
            preloadVnfListBuilder.setPreloadData(preloadDataBuilder.build());
            log.error(
                RETURNED_FAILED_STR + svcOperation + " [" + preloadName + "," + preloadType + "] error code: '"
                    + errorCode + "', Reason: '" + errorMessage + "'");
            try {
                savePreloadList(preloadVnfListBuilder.build(), true, LogicalDatastoreType.CONFIGURATION);
            } catch (Exception e) {
                log.error(
                    CAUGHT_EXCEPTION_STR + svcOperation + " [" + preloadName + "," + preloadType
                        + "] \n", e);

            }
            log.debug("Sending Success rpc result due to external error");
            RpcResult<PreloadNetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadNetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                    .build();
            return Futures.immediateFuture(rpcResult);
        }

        // Got success from SLI
        try {
            preloadData = preloadDataBuilder.build();
            log.info(
                UPDATING_MD_SAL_STR + svcOperation + " [" + preloadName + "," + preloadType + "] preloadData: "
                    + preloadData);
            // svc-configuration-list
            VnfPreloadListBuilder preloadVnfListBuilder = new VnfPreloadListBuilder();
            preloadVnfListBuilder.setVnfName(preloadName);
            preloadVnfListBuilder.setVnfType(preloadType);
            preloadVnfListBuilder.setPreloadData(preloadData);

            // SDNGC-989 set merge flag to false
            savePreloadList(preloadVnfListBuilder.build(), false, LogicalDatastoreType.CONFIGURATION);
            log.info(UPDATING_OPERATIONAL_TREE_STR);
            savePreloadList(preloadVnfListBuilder.build(), false, LogicalDatastoreType.OPERATIONAL);
        } catch (Exception e) {
            log.error(CAUGHT_EXCEPTION_STR + svcOperation + " [" + preloadName + "," + preloadType
                + "] \n", e);
            responseBuilder.setResponseCode("500");
            responseBuilder.setResponseMessage(e.toString());
            responseBuilder.setAckFinalIndicator("Y");
            log.error(RETURNED_FAILED_STR + svcOperation + " [" + preloadName + "," + preloadType + "] "
                + responseBuilder.build());
            RpcResult<PreloadNetworkTopologyOperationOutput> rpcResult =
                RpcResultBuilder.<PreloadNetworkTopologyOperationOutput>status(false)
                    .withResult(responseBuilder.build()).build();
            return Futures.immediateFuture(rpcResult);
        }

        // Update succeeded
        responseBuilder.setResponseCode(errorCode);
        responseBuilder.setAckFinalIndicator(ackFinal);
        if (errorMessage != null) {
            responseBuilder.setResponseMessage(errorMessage);
        }
        log.info(UPDATED_MD_SAL_STR + svcOperation + " [" + preloadName + "," + preloadType + "] ");
        log.info(
            RETURNED_SUCCESS_STR + svcOperation + " [" + preloadName + "," + preloadType + "] " + responseBuilder
                .build());

        RpcResult<PreloadNetworkTopologyOperationOutput> rpcResult =
            RpcResultBuilder.<PreloadNetworkTopologyOperationOutput>status(true).withResult(responseBuilder.build())
                .build();
        return Futures.immediateFuture(rpcResult);
    }
}
