<template>
  <v-dialog v-model="open" max-width="640" :persistent="props.force">
    <v-card rounded="xl">
      <v-card-title class="d-flex align-center justify-space-between">
        <span class="text-h6 font-weight-bold">Créer une école</span>
        <v-btn
          v-if="!props.force"
          icon
          variant="text"
          @click="open = false"
          aria-label="Fermer"
        >
          <v-icon>mdi-close</v-icon>
        </v-btn>
      </v-card-title>

      <v-card-text>
        <v-form ref="formRef" @submit.prevent="onSubmit">
          <v-row dense>
            <v-col cols="12">
              <v-text-field
                v-model="form.nom"
                label="Nom de l’école"
                variant="outlined"
                :rules="[rules.required]"
                autocomplete="off"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.city"
                label="Ville"
                variant="outlined"
                autocomplete="off"
              />
            </v-col>
            <v-col cols="12" sm="6">
              <v-text-field
                v-model="form.postalCode"
                label="Code postal"
                variant="outlined"
                autocomplete="off"
              />
            </v-col>
            <v-col cols="12">
              <v-text-field
                v-model="form.address"
                label="Adresse"
                variant="outlined"
                autocomplete="off"
              />
            </v-col>
          </v-row>

          <div class="d-flex justify-end mt-4" style="gap: 8px">
            <v-btn v-if="!props.force" variant="text" @click="open = false"
              >Annuler</v-btn
            >
            <v-btn type="submit" color="primary" :loading="loading"
              >Créer</v-btn
            >
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
  force: { type: Boolean, default: false },
});

const emit = defineEmits(["update:modelValue", "submit"]);

const open = ref(props.modelValue);
watch(
  () => props.modelValue,
  (v) => (open.value = v)
);
watch(open, (v) => emit("update:modelValue", v));

const formRef = ref(null);
const form = reactive({ nom: "", city: "", postalCode: "", address: "" });

const rules = {
  required: (v) => (!!v && String(v).trim().length > 0) || "Champ requis",
};

watch(open, (v) => {
  if (!v) return;
  form.nom = "";
  form.city = "";
  form.postalCode = "";
  form.address = "";
});

async function onSubmit() {
  if (props.loading) return;
  const res = await formRef.value?.validate?.();
  if (res && res.valid === false) return;

  emit("submit", {
    nom: form.nom,
    city: form.city,
    postalCode: form.postalCode,
    address: form.address,
  });
}
</script>
