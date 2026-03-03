package com.vulsoft.kotighiai;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CyberActivityModern extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private ImageButton btnSwitchToClassic;
    private TextView txtProtectionStatus, txtThreatsBlocked, txtScansToday, txtRiskLevel;
    private Chip chipStatus;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ExtendedFloatingActionButton fabQuickScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cyber_modern);

        toolbar = findViewById(R.id.toolbar);
        btnSwitchToClassic = findViewById(R.id.btnSwitchToClassic);
        txtProtectionStatus = findViewById(R.id.txtProtectionStatus);
        txtThreatsBlocked = findViewById(R.id.txtThreatsBlocked);
        txtScansToday = findViewById(R.id.txtScansToday);
        txtRiskLevel = findViewById(R.id.txtRiskLevel);
        chipStatus = findViewById(R.id.chipStatus);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        fabQuickScan = findViewById(R.id.fabQuickScan);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Bouton pour basculer vers l'interface classique
        btnSwitchToClassic.setOnClickListener(v -> {
            Intent intent = new Intent(this, CyberActivity.class);
            startActivity(intent);
            finish();
        });

        setupViewPager();
        updateMetrics();

        fabQuickScan.setOnClickListener(v -> performQuickScan());
    }

    private void setupViewPager() {
        CyberPagerAdapter adapter = new CyberPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Scanner Ports");
                    tab.setIcon(R.drawable.target);
                    break;
                case 1:
                    tab.setText("Scan Réseau");
                    tab.setIcon(R.drawable.network);
                    break;
                case 2:
                    tab.setText("Vulnérabilités");
                    tab.setIcon(R.drawable.bug);
                    break;
                case 3:
                    tab.setText("Monitoring");
                    tab.setIcon(R.drawable.shield);
                    break;
            }
        }).attach();
    }

    private void updateMetrics() {
        // Simulation - remplacer par appel API
        txtProtectionStatus.setText("ACTIF");
        txtThreatsBlocked.setText("0");
        txtScansToday.setText("0");
        txtRiskLevel.setText("FAIBLE");
        chipStatus.setText("SÉCURISÉ");
    }

    private void performQuickScan() {
        Toast.makeText(this, "Scan rapide en cours...", Toast.LENGTH_SHORT).show();

        // Simulation - remplacer par appel API
        new android.os.Handler().postDelayed(() -> {
            txtScansToday.setText(String.valueOf(Integer.parseInt(txtScansToday.getText().toString()) + 1));
            Toast.makeText(this, "Scan terminé - Aucune menace détectée", Toast.LENGTH_SHORT).show();
        }, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Toast.makeText(this, "Paramètres", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_auto_update) {
            // Lancer le scan automatique des réseaux
            Intent intent = new Intent(this, NetworkScanActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_notifications) {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class CyberPagerAdapter extends FragmentStateAdapter {

        public CyberPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new PortScannerFragment();
                case 1:
                    return new NetworkScanFragment();
                case 2:
                    return new VulnerabilityFragment();
                case 3:
                    return new RealtimeMonitorFragment();
                default:
                    return new PortScannerFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
