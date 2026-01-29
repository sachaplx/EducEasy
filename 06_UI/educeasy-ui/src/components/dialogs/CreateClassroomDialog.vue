<template>
  <v-dialog v-model="open" max-width="640">
    <v-card rounded="xl">
      <v-card-title
        class="d-flex align-center"
        style="gap: 8px; flex-wrap: nowrap"
      >
        <span
          class="text-h6 font-weight-bold"
          style="
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          "
          >Créer une classe</span
        >
        <v-btn
          icon
          variant="text"
          @click="open = false"
          aria-label="Fermer"
          style="flex-shrink: 0"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-alert v-if="disabled" type="warning" variant="tonal" class="mb-4">
          Sélectionne une école avant de créer une classe.
        </v-alert>

        <v-form ref="formRef" @submit.prevent="onSubmit">
          <v-row dense>
            <v-col cols="12">
              <v-text-field
                v-model="form.nom"
                label="Nom de la classe"
                variant="outlined"
                :rules="[rules.required]"
                autocomplete="off"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.niveau"
                label="Niveau"
                variant="outlined"
                autocomplete="off"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.anneeScolaire"
                label="Année scolaire (ex: 2025-2026)"
                variant="outlined"
                :rules="[rules.required]"
                autocomplete="off"
              />
            </v-col>
          </v-row>

          <div class="d-flex justify-end mt-4" style="gap: 8px">
            <v-btn variant="text" @click="open = false">Annuler</v-btn>
            <v-btn
              type="submit"
              color="primary"
              :loading="loading"
              :disabled="disabled"
            >
              Créer
            </v-btn>
          </div>
        </v-form>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>

<script setup>
import { reactive, ref, watch } from "vue";

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  defaultYear: { type: String, default: "" }, // optionnel
});

const emit = defineEmits(["update:modelValue", "submit"]);

const open = ref(props.modelValue);
watch(
  () => props.modelValue,
  (v) => (open.value = v),
);
watch(open, (v) => emit("update:modelValue", v));

const formRef = ref(null);
const form = reactive({ nom: "", niveau: "", anneeScolaire: "" });

const rules = {
  required: (v) => (!!v && String(v).trim().length > 0) || "Champ requis",
};

watch(open, (v) => {
  if (!v) return;
  form.nom = "";
  form.niveau = "";
  form.anneeScolaire = props.defaultYear || form.anneeScolaire || "";
});

async function onSubmit() {
  if (props.loading || props.disabled) return;
  const res = await formRef.value?.validate?.();
  if (res && res.valid === false) return;

  emit("submit", {
    nom: form.nom,
    niveau: form.niveau,
    anneeScolaire: form.anneeScolaire,
  });
}
</script>
