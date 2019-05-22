/*
 * Copyright (C) 2016 The Android Open Source Project
 *
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
 */
package android.car.cluster;

import android.car.cluster.renderer.InstrumentClusterRenderingService;
import android.car.cluster.renderer.NavigationRenderer;
import android.car.navigation.CarNavigationInstrumentCluster;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;

import androidx.car.cluster.navigation.NavigationState;
import androidx.versionedparcelable.ParcelUtils;

import com.google.android.collect.Lists;

/**
 * Dummy implementation of {@link LoggingClusterRenderingService} to log all interaction.
 */
public class LoggingClusterRenderingService extends InstrumentClusterRenderingService {
    private static final String TAG = LoggingClusterRenderingService.class.getSimpleName();
    private static final String NAV_STATE_BUNDLE_KEY = "navstate";
    private static final int NAV_STATE_EVENT_ID = 1;

    @Override
    protected NavigationRenderer getNavigationRenderer() {
        NavigationRenderer navigationRenderer = new NavigationRenderer() {
            @Override
            public CarNavigationInstrumentCluster getNavigationProperties() {
                Log.i(TAG, "getNavigationProperties");
                CarNavigationInstrumentCluster config =
                        CarNavigationInstrumentCluster.createCluster(1000);
                config.getExtra().putIntegerArrayList("dummy", Lists.newArrayList(1, 2, 3, 4));
                Log.i(TAG, "getNavigationProperties, returns: " + config);
                return config;
            }

            @Override
            public void onEvent(int eventType, Bundle bundle) {
                StringBuilder bundleSummary = new StringBuilder();
                if (eventType == NAV_STATE_EVENT_ID) {
                    bundle.setClassLoader(ParcelUtils.class.getClassLoader());
                    NavigationState navState = NavigationState
                            .fromParcelable(bundle.getParcelable(NAV_STATE_BUNDLE_KEY));
                    bundleSummary.append(navState.toString());

                    // Sending broadcast for testing.
                    Intent intent = new Intent("android.car.cluster.NAVIGATION_STATE_UPDATE");
                    intent.putExtra(NAV_STATE_BUNDLE_KEY, bundle);
                    sendBroadcastAsUser(intent, UserHandle.ALL);
                } else {
                    for (String key : bundle.keySet()) {
                        bundleSummary.append(key);
                        bundleSummary.append("=");
                        bundleSummary.append(bundle.get(key));
                        bundleSummary.append(" ");
                    }
                }
                Log.i(TAG, "onEvent(" + eventType + ", " + bundleSummary + ")");
            }
        };

        Log.i(TAG, "createNavigationRenderer, returns: " + navigationRenderer);
        return navigationRenderer;
    }
}
