<template>
  <v-card rounded="xl" class="pa-4 soft-card">
    <div class="d-flex align-center justify-space-between">
      <div>
        <div class="text-body-2 text-medium-emphasis">Taux de présence</div>
        <div class="text-h5 font-weight-bold mt-1">
          <template v-if="loading">
            <v-progress-circular indeterminate size="24" width="2" />
          </template>
          <template v-else-if="error">
            <span class="text-error">Erreur</span>
          </template>
          <template v-else-if="value !== null && value !== undefined">
            {{ value }}%
          </template>
          <template v-else>
            <span class="text-medium-emphasis">N/C</span>
          </template>
        </div>
      </div>

      <v-tooltip :text="error || 'Actualiser'" location="top">
        <template #activator="{ props: tooltipProps }">
          <v-btn
            v-bind="tooltipProps"
            icon
            size="small"
            variant="text"
            :loading="loading"
            @click="$emit('refresh')"
            aria-label="Actualiser le taux de présence"
          >
            <v-icon>mdi-refresh</v-icon>
          </v-btn>
        </template>
      </v-tooltip>
    </div>

    <div class="text-caption text-medium-emphasis mt-2">
      {{ subtitle }}
    </div>

    <v-progress-linear
      class="mt-3"
      :model-value="value || 0"
      height="8"
      rounded
      :color="error ? 'error' : 'primary'"
    />

    <div v-if="error" class="text-caption text-error mt-2">
      {{ error }}
    </div>
  </v-card>
</template>

<script setup>
defineProps({
  value: { type: Number, default: null },
  subtitle: { type: String, default: "Cette semaine" },
  loading: { type: Boolean, default: false },
  error: { type: String, default: "" },
});

defineEmits(["refresh"]);
</script>

<style scoped>
.soft-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(6px);
  border: 1px solid rgba(0, 0, 0, 0.06);
}
</style>
