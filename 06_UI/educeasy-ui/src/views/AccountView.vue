<template>
  <v-container class="py-8" style="max-width: 960px">
    <v-row>
      <v-col cols="12">
        <h1 class="text-h5 mb-4">Mon compte</h1>
      </v-col>

      <!-- Infos du compte -->
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-subtitle-1 py-3">
            Informations du compte
          </v-card-title>
          <v-divider />
          <v-card-text>
            <v-skeleton-loader
              v-if="loadingProfile"
              type="list-item-two-line"
            />

            <template v-else>
              <div class="mb-4">
                <div class="text-caption text-medium-emphasis">
                  Nom d'utilisateur
                </div>
                <div class="text-body-1">{{ profile?.username }}</div>
              </div>

              <div class="mb-4">
                <div class="text-caption text-medium-emphasis">Rôle</div>
                <div class="text-body-1">
                  {{ profile?.role }}
                </div>
              </div>

              <v-form ref="emailForm" @submit.prevent="openEmailDialog">
                <v-text-field
                  v-model="email"
                  label="Adresse e-mail"
                  type="email"
                  variant="outlined"
                  :disabled="loadingEmail || emailDialogLoading"
                  :rules="[rules.required, rules.email]"
                  class="mb-3"
                />

                <v-btn
                  type="submit"
                  class="button"
                  :loading="loadingEmail || emailDialogLoading"
                  :disabled="loadingEmail || emailDialogLoading"
                  height="40"
                  @click="openEmailDialog"
                >
                  Mettre à jour l'e-mail
                </v-btn>
              </v-form>

              <v-alert
                v-if="emailMessage"
                :type="emailError ? 'error' : 'success'"
                variant="tonal"
                class="mt-3"
                density="comfortable"
              >
                {{ emailMessage }}
              </v-alert>

              <v-dialog v-model="emailDialog" max-width="420">
                <v-card rounded="xl">
                  <v-card-title class="text-subtitle-1">
                    Confirmer la modification
                  </v-card-title>
                  <v-card-text>
                    <div class="text-body-2 mb-3">
                      Pour modifier votre adresse e-mail en
                      <strong>{{ email }}</strong
                      >, veuillez confirmer votre mot de passe.
                    </div>

                    <v-text-field
                      v-model="emailPassword"
                      :type="showEmailPassword ? 'text' : 'password'"
                      label="Mot de passe"
                      variant="outlined"
                      :append-inner-icon="
                        showEmailPassword ? 'mdi-eye' : 'mdi-eye-off'
                      "
                      @click:append-inner="
                        showEmailPassword = !showEmailPassword
                      "
                      :disabled="emailDialogLoading"
                      class="mb-2"
                    />

                    <v-alert
                      v-if="emailDialogError"
                      type="error"
                      variant="tonal"
                      density="comfortable"
                      class="mt-1"
                    >
                      {{ emailDialogError }}
                    </v-alert>
                  </v-card-text>
                  <v-card-actions class="justify-end">
                    <v-btn
                      variant="text"
                      @click="emailDialog = false"
                      :disabled="emailDialogLoading"
                    >
                      Annuler
                    </v-btn>
                    <v-btn
                      class="button"
                      @click="confirmEmailChange"
                      :loading="emailDialogLoading"
                    >
                      Confirmer
                    </v-btn>
                  </v-card-actions>
                </v-card>
              </v-dialog>
            </template>
          </v-card-text>
        </v-card>
      </v-col>

      <!-- Changement de mot de passe -->
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-subtitle-1 py-3">
            Changer le mot de passe
          </v-card-title>
          <v-divider />
          <v-card-text>
            <v-form ref="passwordForm" @submit.prevent="submitPassword">
              <v-text-field
                v-model="currentPassword"
                :type="showCurrent ? 'text' : 'password'"
                label="Mot de passe actuel"
                variant="outlined"
                :append-inner-icon="showCurrent ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showCurrent = !showCurrent"
                :disabled="loadingPassword"
                :rules="[rules.required]"
                class="mb-3"
              />
              <v-text-field
                v-model="newPassword"
                :type="showNew ? 'text' : 'password'"
                label="Nouveau mot de passe"
                variant="outlined"
                :append-inner-icon="showNew ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showNew = !showNew"
                :disabled="loadingPassword"
                :rules="[rules.required]"
                class="mb-3"
              />
              <v-text-field
                v-model="newPasswordConfirm"
                :type="showNew ? 'text' : 'password'"
                label="Confirmer le nouveau mot de passe"
                variant="outlined"
                :disabled="loadingPassword"
                :rules="[rules.required, rules.matchPassword]"
                class="mb-3"
              />

              <v-btn
                type="submit"
                class="button"
                :loading="loadingPassword"
                :disabled="loadingPassword"
                height="40"
              >
                Mettre à jour le mot de passe
              </v-btn>
            </v-form>

            <v-alert
              v-if="passwordMessage"
              :type="passwordError ? 'error' : 'success'"
              variant="tonal"
              class="mt-3"
              density="comfortable"
            >
              {{ passwordMessage }}
            </v-alert>

            <div class="text-caption text-medium-emphasis mt-2">
              Utilisez au minimum 8 caractères avec des lettres et des chiffres.
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useProfileStore } from "../stores/profile";

const profileStore = useProfileStore();

const loadingProfile = ref(true);
const loadingEmail = ref(false);
const loadingPassword = ref(false);

const emailForm = ref(null);
const passwordForm = ref(null);

const email = ref("");
const emailMessage = ref("");
const emailError = ref(false);

const emailDialog = ref(false);
const emailDialogLoading = ref(false);
const emailDialogError = ref("");
const emailPassword = ref("");
const showEmailPassword = ref(false);

const currentPassword = ref("");
const newPassword = ref("");
const newPasswordConfirm = ref("");
const passwordMessage = ref("");
const passwordError = ref(false);

const showCurrent = ref(false);
const showNew = ref(false);

const profile = computed(() => profileStore.profile);

const rules = {
  required: (v) => !!v || "Ce champ est requis",
  email: (v) =>
    !v ||
    /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(v) ||
    "Adresse e-mail invalide",
  matchPassword: (v) =>
    v === newPassword.value || "Les mots de passe ne correspondent pas",
};

onMounted(async () => {
  try {
    const data = await profileStore.loadProfile();
    email.value = data.email;
  } catch (e) {
    console.error("Failed to load profile", e);
  } finally {
    loadingProfile.value = false;
  }
});

async function openEmailDialog() {
  emailMessage.value = "";
  emailError.value = false;
  emailDialogError.value = "";
  const valid = await emailForm.value?.validate();
  if (!valid) return;

  if (
    !email.value ||
    !/^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/.test(email.value)
  ) {
    return;
  }

  if (profile.value && email.value === profile.value.email) {
    emailError.value = true;
    emailMessage.value = "Cette adresse est déjà associée à votre compte.";
    return;
  }

  if (profile.value) emailPassword.value = "";
  showEmailPassword.value = false;
  emailDialog.value = true;
}

async function confirmEmailChange() {
  emailDialogLoading.value = true;
  emailDialogError.value = "";
  emailError.value = false;
  emailMessage.value = "";

  try {
    if (!emailPassword.value) {
      emailDialogError.value = "Veuillez saisir votre mot de passe.";
      return;
    }

    const res = await profileStore.updateEmail(
      email.value,
      emailPassword.value,
    );
    emailMessage.value = res?.message || "Adresse e-mail mise à jour.";
    emailError.value = false;

    emailDialog.value = false;
    emailPassword.value = "";
  } catch (e) {
    const s = e?.response?.status;
    const err = e?.response?.data?.error || "";
    const msg = e?.response?.data?.message || "";

    // 🔹 Cas "email déjà utilisé par un autre compte"
    if (msg.toLowerCase().includes("déjà utilisé")) {
      emailDialogError.value = msg;
      emailError.value = true;
      return;
    }

    // 🔹 Autres erreurs (mot de passe incorrect, etc.)
    emailError.value = true;
    emailMessage.value = msg || "Impossible de mettre à jour l'e-mail.";
    emailDialogError.value =
      "Mot de passe incorrect ou erreur lors de la mise à jour.";
  } finally {
    emailDialogLoading.value = false;
  }
}

async function submitPassword() {
  passwordMessage.value = "";
  passwordError.value = false;
  loadingPassword.value = true;
  try {
    const valid = await passwordForm.value?.validate();
    if (!valid) return;

    const res = await profileStore.changePassword(
      currentPassword.value,
      newPassword.value,
    );
    passwordMessage.value = res?.message || "Mot de passe mis à jour.";

    // reset des champs
    currentPassword.value = "";
    newPassword.value = "";
    newPasswordConfirm.value = "";
  } catch (e) {
    const s = e?.response?.status;
    const msg =
      e?.response?.data?.error || e?.response?.data?.message || e?.message;
    passwordError.value = true;
    passwordMessage.value = `Erreur (${s ?? "network"}) : ${
      msg ?? "Impossible de mettre à jour le mot de passe."
    }`;
  } finally {
    loadingPassword.value = false;
  }
}
</script>

<style scoped>
.button {
  background: linear-gradient(90deg, #2563eb, #3b82f6) !important;
  color: white !important;
}
</style>
