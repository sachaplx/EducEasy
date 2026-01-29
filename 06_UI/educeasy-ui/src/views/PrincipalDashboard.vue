<template>
  <v-container fluid class="dashboard">
    <!-- Header -->
    <div class="dashboard__inner" style="gap: 12px">
      <div>
        <div class="text-h4 font-weight-bold">Bonjour, {{ displayName }}</div>
        <div class="text-body-2 text-medium-emphasis">
          Voici un aperçu de votre réseau scolaire aujourd’hui
        </div>
      </div>

      <v-alert v-if="auth.meError" type="error" variant="tonal" class="mb-4">
        {{ auth.meError }}
      </v-alert>

      <v-row dense>
        <!-- Left: Classes by school -->
        <v-col cols="12" md="8">
          <v-card rounded="xl" class="pa-4">
            <div
              class="d-flex flex-wrap align-center justify-space-between"
              style="gap: 12px"
            >
              <div>
                <div class="text-h6 font-weight-bold">Classes par école</div>
                <div class="text-body-2 text-medium-emphasis">
                  Gérez vos classes et suivez la progression
                </div>
              </div>

              <div class="d-flex align-center flex-wrap" style="gap: 8px">
                <v-select
                  v-model="selectedEcoleId"
                  :items="ecoles"
                  item-title="nom"
                  item-value="id"
                  label="École"
                  variant="outlined"
                  density="comfortable"
                  hide-details
                  :loading="school.loading.schools"
                  style="min-width: 280px; flex: 1 1 auto"
                >
                  <template #no-data>
                    <div class="text-center pa-4 text-medium-emphasis">
                      Aucune école disponible
                    </div>
                  </template>
                </v-select>

                <div
                  class="d-flex align-center"
                  style="gap: 8px; margin-left: auto"
                >
                  <v-tooltip text="Créer une nouvelle école" location="top">
                    <template #activator="{ props }">
                      <v-btn
                        v-bind="props"
                        type="submit"
                        class="button"
                        :loading="loadingCreate.school"
                        @click="openCreateSchool"
                        aria-label="Créer une nouvelle école"
                        prepend-icon="mdi-school"
                      >
                        Nouvelle école
                      </v-btn>
                    </template>
                  </v-tooltip>
                  <v-tooltip text="Actualiser" location="top">
                    <template #activator="{ props }">
                      <v-btn
                        v-bind="props"
                        icon
                        variant="text"
                        :loading="school.loading.classrooms"
                        :disabled="!selectedEcoleId"
                        @click="loadClasses"
                        aria-label="Actualiser"
                      >
                        <v-icon>mdi-refresh</v-icon>
                      </v-btn>
                    </template>
                  </v-tooltip>
                </div>
              </div>
            </div>

            <div class="mt-4">
              <template v-if="!selectedEcoleId && !school.loading.schools">
                <div class="text-medium-emphasis py-6">
                  Sélectionnez une école pour afficher ses classes.
                </div>
              </template>

              <template v-else-if="school.loading.classrooms">
                <v-row dense>
                  <v-col v-for="i in 4" :key="i" cols="12" sm="6">
                    <v-skeleton-loader type="card" />
                  </v-col>
                </v-row>
              </template>

              <template v-else-if="selectedEcoleId && !classes.length">
                <div class="text-medium-emphasis py-6">
                  Aucune classe pour cette école.
                </div>
                <v-btn
                  class="button mt-4"
                  variant="text"
                  block
                  @click="openCreateClass"
                  prepend-icon="mdi-google-classroom"
                  >Nouvelle classe</v-btn
                >
              </template>

              <template v-else>
                <v-text-field
                  v-model="classQuery"
                  label="Rechercher une classe"
                  variant="outlined"
                  density="comfortable"
                  hide-details
                  prepend-inner-icon="mdi-magnify"
                  clearable
                  class="mb-4"
                  placeholder="Nom, enseignant, cycle..."
                />

                <div
                  v-if="!classesFiltered.length"
                  class="text-medium-emphasis py-6 text-center"
                >
                  Aucune classe ne correspond à votre recherche.
                </div>

                <div v-else class="class-grid">
                  <ClassCards
                    v-for="c in classesFiltered"
                    :key="c.id"
                    :name="classLabel(c)"
                    :level="cycleLabel(c)"
                    :teacher-name="teacherLabel(c)"
                    :teacher-initials="
                      initials(c.teacherFirstName, c.teacherLastName)
                    "
                    :student-count="pupilsCount(c)"
                    :course-count="coursesCount(c)"
                    :color="pickColor(c)"
                    @click="openClass(c)"
                  />
                </div>
                <v-btn
                  class="button mt-4"
                  variant="text"
                  block
                  @click="openCreateClass"
                  prepend-icon="mdi-google-classroom"
                  >Nouvelle classe</v-btn
                >
              </template>
            </div>
          </v-card>

          <!-- Actions rapides 
          <div class="mt-6">
            <div class="text-h6 font-weight-bold mb-3">Actions rapides</div>
            <v-row dense>
              <v-col cols="12" sm="6" md="3">
                <QuickAction
                  title="Ajouter un élève"
                  icon="mdi-account-plus"
                  color="primary"
                  @click="actionAddPupil"
                />
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <QuickAction
                  title="Nouvelle classe"
                  icon="mdi-google-classroom"
                  color="deep-orange"
                  @click="openCreateClass"
                />
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <QuickAction
                  title="Créer un rapport"
                  icon="mdi-file-document-outline"
                  color="grey-darken-2"
                  @click="actionCreateReport"
                />
              </v-col>
              <v-col cols="12" sm="6" md="3">
                <QuickAction
                  title="Planifier"
                  icon="mdi-calendar"
                  color="grey-darken-2"
                  @click="actionSchedule"
                />
              </v-col>
            </v-row>
          </div> -->
        </v-col>

        <!-- Right: Recent activity -->
        <v-col cols="12" md="4"
          ><PresenceCard
            class="mb-4"
            :value="presenceRateDisplay"
            subtitle="Cette semaine"
            :loading="pupil.presence.loading"
            :error="pupil.presence.error"
            @refresh="pupil.fetchPresenceRate()"
          />
          <!-- <v-card rounded="xl" class="pa-4">
            <div class="d-flex align-center justify-space-between mb-2">
              <div class="text-h6 font-weight-bold">Activité récente</div>
              <v-btn
                variant="text"
                size="small"
                color="primary"
                @click="goToActivity"
              >
                Tout voir
              </v-btn>
            </div>

            <v-list lines="two" density="comfortable">
              <v-list-item
                v-for="(a, idx) in recentActivity"
                :key="idx"
                class="rounded-lg mb-1"
              >
                <template #prepend>
                  <v-avatar :color="a.color" variant="tonal" size="34">
                    <v-icon>{{ a.icon }}</v-icon>
                  </v-avatar>
                </template>

                <v-list-item-title class="font-weight-medium">
                  {{ a.title }}
                </v-list-item-title>
                <v-list-item-subtitle class="text-medium-emphasis">
                  {{ a.subtitle }}
                </v-list-item-subtitle>

                <template #append>
                  <div class="text-caption text-medium-emphasis">
                    {{ a.when }}
                  </div>
                </template>
              </v-list-item>
            </v-list>

            <v-card
              rounded="xl"
              class="mt-4 pa-4"
              color="primary"
              variant="flat"
            >
              <div class="text-subtitle-2 font-weight-bold text-white mb-1">
                Conseil du jour
              </div>
              <div class="text-body-2 text-white" style="opacity: 0.95">
                Pensez à valider les absences de la semaine avant vendredi.
              </div>
            </v-card>
          </v-card> -->
        </v-col>
      </v-row>

      <!-- Dialog: pupils in class -->
      <v-dialog v-model="pupilsDialog" max-width="720">
        <v-card rounded="xl">
          <v-card-title
            class="d-flex align-center"
            style="gap: 8px; flex-wrap: nowrap"
          >
            <div
              class="d-flex flex-column"
              style="flex: 1; min-width: 0; overflow: hidden"
            >
              <span
                class="text-h6 font-weight-bold"
                style="
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                "
                >{{ classLabel(activeClass) }}</span
              >
              <span
                class="text-body-2 text-medium-emphasis"
                style="
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                "
              >
                {{ teacherLabel(activeClass) }}
              </span>
            </div>

            <div class="d-flex align-center" style="gap: 6px; flex-shrink: 0">
              <v-btn
                icon
                variant="text"
                :loading="pupil.loading.pupils"
                @click="refreshPupils"
                aria-label="Actualiser élèves"
              >
                <v-icon>mdi-refresh</v-icon>
              </v-btn>
              <v-btn
                icon
                variant="text"
                :disabled="!activeClass"
                @click="openAddPupil(activeClass?.id)"
                aria-label="Ajouter un élève"
              >
                <v-icon>mdi-account-plus</v-icon>
              </v-btn>
              <v-btn
                icon
                variant="text"
                :disabled="!activeClass"
                @click="openSetMaitre(activeClass?.id)"
                aria-label="Définir le maître"
              >
                <v-icon>mdi-account-tie</v-icon>
              </v-btn>
              <v-btn
                icon
                variant="text"
                @click="pupilsDialog = false"
                aria-label="Fermer"
              >
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </div>
          </v-card-title>

          <v-card-text>
            <v-text-field
              v-model="pupilQuery"
              label="Rechercher un élève"
              variant="outlined"
              density="comfortable"
              hide-details
              prepend-inner-icon="mdi-magnify"
              class="mb-3"
            />

            <div v-if="pupil.loading.pupils" class="py-8 d-flex justify-center">
              <v-progress-circular indeterminate />
            </div>

            <v-alert
              v-else-if="pupil.error.pupils"
              type="error"
              variant="tonal"
              class="mb-3"
            >
              {{ pupil.error.pupils }}
            </v-alert>

            <div
              v-else-if="!pupil.pupils.length"
              class="text-medium-emphasis py-8 text-center"
            >
              Aucun élève dans cette classe.
            </div>

            <v-list v-else lines="two" density="comfortable">
              <v-list-item
                v-for="p in pupilsFiltered"
                :key="p.id"
                class="rounded mb-1"
                @click="goToPupil(p)"
              >
                <template #prepend>
                  <v-avatar :color="avatarColor(p.gender)" size="32">
                    <span class="avatar-initials">{{
                      initials(p.prenom, p.nom)
                    }}</span>
                  </v-avatar>
                </template>

                <v-list-item-title class="font-weight-medium">
                  {{ p.prenom }} {{ p.nom }}
                </v-list-item-title>

                <v-list-item-subtitle class="text-medium-emphasis">
                  {{ classLabel(activeClass) }}
                </v-list-item-subtitle>

                <template #append>
                  <div class="d-flex align-center" style="gap: 4px">
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      color="primary"
                      @click.stop="handleQuickNote(p)"
                      aria-label="Ajouter une note"
                    >
                      <v-icon size="20">mdi-note-plus-outline</v-icon>
                    </v-btn>
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      color="warning"
                      @click.stop="handleQuickAbsence(p)"
                      aria-label="Marquer absent"
                    >
                      <v-icon size="20">mdi-account-off-outline</v-icon>
                    </v-btn>
                    <v-btn
                      icon
                      size="small"
                      variant="text"
                      @click.stop="goToPupil(p)"
                      aria-label="Ouvrir profil"
                    >
                      <v-icon>mdi-open-in-new</v-icon>
                    </v-btn>
                  </div>
                </template>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-dialog>

      <v-dialog v-model="confirmReplaceMaitre" max-width="520">
        <v-card rounded="xl" class="pa-4">
          <div class="text-h6 font-weight-bold mb-2">Remplacer le maître ?</div>
          <div class="text-body-2 text-medium-emphasis">
            Cette classe a déjà un maître ({{ teacherLabel(activeClass) }}).
            Voulez-vous le remplacer ?
          </div>

          <div class="d-flex justify-end mt-4" style="gap: 8px">
            <v-btn variant="text" @click="confirmReplaceMaitre = false"
              >Annuler</v-btn
            >
            <v-btn
              color="primary"
              @click="
                confirmReplaceMaitre = false;
                doSetMaitre(pendingMaitreEmail);
              "
            >
              Remplacer
            </v-btn>
          </div>
        </v-card>
      </v-dialog>

      <CreateSchoolDialog
        v-model="createSchoolDialog"
        :loading="loadingCreate.school"
        :force="mustCreateSchool"
        @submit="handleCreateSchool"
      />

      <CreateClassroomDialog
        v-model="createClassDialog"
        :loading="loadingCreate.classroom"
        :disabled="!selectedEcoleId"
        :default-year="defaultSchoolYear()"
        @submit="handleCreateClassroom"
      />

      <AddPupilDialog
        v-model="addPupilDialog"
        :loading="loadingCreate.pupil"
        :classroom-id="activeClassId"
        @submit="handleAddPupil"
      />

      <SetMaitreDialog
        v-model="setMaitreDialog"
        :loading="loadingCreate.maitre"
        :classroom-id="activeClassId"
        @submit="handleSetMaitre"
      />

      <!-- Quick Actions Dialogs -->
      <AddNoteDialog
        v-if="selectedPupil"
        v-model="quickNoteDialog"
        :pupil-id="selectedPupil.id"
        @saved="refreshPupils"
      />

      <QuickAbsenceDialog
        v-if="selectedPupil"
        v-model="quickAbsenceDialog"
        :pupils="[selectedPupil]"
        @saved="refreshPupils"
      />

      <!-- Confirmation Dialog -->
      <ConfirmDialog
        v-model="confirmDialog.show"
        :title="confirmDialog.title"
        :message="confirmDialog.message"
        :type="confirmDialog.type"
        :loading="confirmDialog.loading"
        @confirm="handleConfirmAction"
        @cancel="handleCancelConfirm"
      />
    </div>
  </v-container>
</template>

<script setup>
import {
  computed,
  onMounted,
  reactive,
  ref,
  defineComponent,
  watch,
} from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import { usePupilStore } from "../stores/pupil";
import { useSchoolStore } from "../stores/school";
import { useToastStore } from "../stores/toast";
import KpiCard from "../components/dashboard/KpiCards.vue";
import QuickAction from "../components/dashboard/QuickActions.vue";
import ClassCards from "../components/dashboard/ClassCards.vue";
import PresenceCard from "../components/dashboard/PresenceCard.vue";
import CreateSchoolDialog from "../components/dialogs/CreateSchoolDialog.vue";
import CreateClassroomDialog from "../components/dialogs/CreateClassroomDialog.vue";
import SetMaitreDialog from "../components/dialogs/SetMaitreDialog.vue";
import AddPupilDialog from "../components/dialogs/AddPupilDialog.vue";
import AddNoteDialog from "../components/dialogs/AddNoteDialog.vue";
import QuickAbsenceDialog from "../components/dialogs/QuickAbsenceDialog.vue";
import ConfirmDialog from "../components/dialogs/ConfirmDialog.vue";

/* -----------------------------
  State
------------------------------ */
const router = useRouter();
const auth = useAuthStore();
const pupil = usePupilStore();
const school = useSchoolStore();
const toast = useToastStore();

const confirmReplaceMaitre = ref(false);
const pendingMaitreEmail = ref("");

const confirmDialog = ref({
  show: false,
  loading: false,
  type: "default",
  title: "",
  message: "",
  action: null,
  data: null,
});

const error = ref("");

const me = ref(null);

const presence = ref({
  loading: false,
  error: null,
  rate: 0,
  from: null,
  to: null,
});

const createSchoolDialog = ref(false);
const createClassDialog = ref(false);
const setMaitreDialog = ref(false);
const addPupilDialog = ref(false);
const quickNoteDialog = ref(false);
const quickAbsenceDialog = ref(false);
const selectedPupil = ref(null);

const loadingCreate = reactive({
  school: false,
  classroom: false,
  maitre: false,
  pupil: false,
});

const pupilsDialog = ref(false);
const activeClassId = ref(null);
const pupilQuery = ref("");
const classQuery = ref("");

function defaultSchoolYear() {
  const now = new Date();
  const year = now.getFullYear();
  const month = now.getMonth() + 1;
  const start = month >= 8 ? year : year - 1;
  return `${start}-${start + 1}`;
}
/* -----------------------------
  Computed
------------------------------ */
const displayName = computed(
  () => auth.me?.firstName || auth.me?.lastName || auth.username || "",
);

const ecoles = computed(() => school.schools);
const classes = computed(() => school.classrooms);
const selectedEcoleId = computed({
  get: () => school.selectedEcoleId,
  set: (v) => school.setSelectedEcole(v),
});

const presenceRateDisplay = computed(() => {
  const r = pupil.presence.rate;
  return typeof r === "number" ? Math.round(r) : null;
});

const activeClass = computed(
  () =>
    classes.value.find((c) => String(c.id) === String(activeClassId.value)) ||
    null,
);

const activeState = computed(() => state(activeClassId.value));

const classesFiltered = computed(() => {
  const list = classes.value || [];
  const q = (classQuery.value || "").trim().toLowerCase();
  if (!q) return list;
  return list.filter((c) => {
    const name = classLabel(c).toLowerCase();
    const teacher = teacherLabel(c).toLowerCase();
    const cycle = cycleLabel(c).toLowerCase();
    return name.includes(q) || teacher.includes(q) || cycle.includes(q);
  });
});

const pupilsFiltered = computed(() => {
  const list = pupil.pupils || [];
  const q = (pupilQuery.value || "").trim().toLowerCase();
  if (!q) return list;
  return list.filter((p) => {
    const nom = (p.nom || "").toLowerCase();
    const prenom = (p.prenom || "").toLowerCase();
    return nom.includes(q) || prenom.includes(q);
  });
});

const mustCreateSchool = computed(() => {
  auth.isPrincipal &&
    !school.loading.schools &&
    (school.schools?.length ?? 0) === 0;
});

const kpis = computed(() => {
  // NOTE: présence & deltas = placeholders (à brancher)
  const totalClasses = classes.value.length || 0;

  // total enseignants: unique sur teacher name/id si dispo
  const teachers = new Set(
    classes.value
      .map((c) =>
        `${c.teacherFirstName || ""}|${c.teacherLastName || ""}`.trim(),
      )
      .filter((x) => x !== "|"),
  );

  // total élèves: si une classe a un count natif, on le prend, sinon on prend cache chargé, sinon 0
  const totalPupils = classes.value.reduce((sum, c) => sum + pupilsCount(c), 0);

  return {
    totalPupils: totalPupils || 0,
    totalClasses,
    totalTeachers: teachers.size || 0,
    presenceRate: presence.value.rate,
  };
});

/* -----------------------------
  Fetch
------------------------------ */
onMounted(async () => {
  await school.fetchMine({ autoSelectFirst: true });
  await pupil.fetchPresenceRate();
  await auth.whoAmI();
});

watch(
  () => school.selectedEcoleId,
  async (id, prev) => {
    if (!id || id === prev) return;
    await school.fetchClassesForSelectedSchool();
    // Preload pupil counts for all classrooms
    if (classes.value.length) {
      const classroomIds = classes.value.map((c) => c.id).filter(Boolean);
      await pupil.fetchCountsForClassrooms(classroomIds);
    }
  },
);

watch(
  mustCreateSchool,
  (must) => {
    if (must) {
      createSchoolDialog.value = true;
    }
  },
  { immediate: true },
);

watch(
  () => school.activeClassId,
  async (id) => {
    if (!pupilsDialog.value) return;
    if (!id) return;
    await pupil.fetchPupilsForClassroom(id);
  },
);

function handleCreateSchool(form) {
  confirmDialog.value = {
    show: true,
    loading: false,
    type: "info",
    title: "Créer une nouvelle école",
    message: `Confirmez-vous la création de l'école "${form.nom || "sans nom"}" ?`,
    action: "createSchool",
    data: form,
  };
}

async function executeCreateSchool(form) {
  loadingCreate.school = true;
  confirmDialog.value.loading = true;
  try {
    await school.createSchool(form);
    createSchoolDialog.value = false;
    confirmDialog.value.show = false;
    toast.success("École créée.");
  } catch (e) {
    toast.error(extractApiError(e, "Erreur lors de la création de l'école."));
  } finally {
    loadingCreate.school = false;
    confirmDialog.value.loading = false;
  }
}

function handleCreateClassroom(form) {
  confirmDialog.value = {
    show: true,
    loading: false,
    type: "info",
    title: "Créer une nouvelle classe",
    message: `Confirmez-vous la création de la classe "${form.nom || "sans nom"}" pour l'année ${form.anneeScolaire || ""} ?`,
    action: "createClassroom",
    data: form,
  };
}

async function executeCreateClassroom(form) {
  loadingCreate.classroom = true;
  confirmDialog.value.loading = true;
  try {
    await school.createClassroom(form);
    createClassDialog.value = false;
    confirmDialog.value.show = false;
    toast.success("Classe créée.");
  } catch (e) {
    toast.error(extractApiError(e, "Erreur lors de la création de la classe."));
  } finally {
    loadingCreate.classroom = false;
    confirmDialog.value.loading = false;
  }
}

async function handleSetMaitre(email) {
  if (activeClass.value?.teacherId) {
    pendingMaitreEmail.value = email;
    confirmReplaceMaitre.value = true;
    return;
  }
  await doSetMaitre(email);
}

async function doSetMaitre(email) {
  loadingCreate.maitre = true;
  try {
    await school.setMaitre(activeClassId.value, email);
    setMaitreDialog.value = false;
    toast.success("Maître défini pour la classe.");
  } catch (e) {
    toast.error(extractApiError(e, "Erreur lors de la définition du maître."));
  } finally {
    loadingCreate.maitre = false;
  }
}

async function openPupilsDialog(classroomId) {
  school.setActiveClass(classroomId);
  pupilsDialog.value = true;
  await pupil.fetchPupilsForClassroom(classroomId);
}

async function refreshPupils() {
  if (!activeClassId.value) return;
  await pupil.reloadPupilsForClassroom(activeClassId.value);
}

async function loadClasses() {
  if (!selectedEcoleId.value) return;
  await school.fetchClassesForSelectedSchool();
}

function handleAddPupil(form) {
  if (!school.activeClassId) return;

  const pupilName =
    `${form.prenom || ""} ${form.nom || ""}`.trim() || "cet élève";
  confirmDialog.value = {
    show: true,
    loading: false,
    type: "info",
    title: "Ajouter un élève",
    message: `Confirmez-vous l'ajout de ${pupilName} à la classe ?`,
    action: "addPupil",
    data: form,
  };
}

async function executeAddPupil(form) {
  if (!school.activeClassId) return;

  loadingCreate.pupil = true;
  confirmDialog.value.loading = true;
  try {
    await pupil.addPupilToClassroom(activeClassId.value, form);
    addPupilDialog.value = false;
    confirmDialog.value.show = false;
    toast.success("Élève ajouté à la classe.");
    await pupil.reloadPupilsForClassroom(activeClassId.value);
  } catch (e) {
    toast.error(extractApiError(e, "Erreur lors de l'ajout de l'élève."));
  } finally {
    loadingCreate.pupil = false;
    confirmDialog.value.loading = false;
  }
}

async function handleConfirmAction() {
  const { action, data } = confirmDialog.value;

  switch (action) {
    case "createSchool":
      await executeCreateSchool(data);
      break;
    case "createClassroom":
      await executeCreateClassroom(data);
      break;
    case "addPupil":
      await executeAddPupil(data);
      break;
    default:
      confirmDialog.value.show = false;
  }
}

function handleCancelConfirm() {
  confirmDialog.value.show = false;
  confirmDialog.value.action = null;
  confirmDialog.value.data = null;
}

/* -----------------------------
  UI actions
------------------------------ */
async function openClass(c) {
  activeClassId.value = c.id;
  school.setActiveClass(c.id);
  pupilsDialog.value = true;
  pupilQuery.value = "";
  await pupil.fetchPupilsForClassroom(c.id);
}

function openCreateSchool() {
  createSchoolDialog.value = true;
}

function openCreateClass() {
  if (!school.selectedEcoleId) {
    toast.warning("Veuillez sélectionner une école avant de créer une classe.");
    return;
  }
  createClassDialog.value = true;
}

function openAddPupil() {
  if (!activeClassId.value) return;
  addPupilDialog.value = true;
}

function handleQuickNote(p) {
  selectedPupil.value = p;
  quickNoteDialog.value = true;
}

function handleQuickAbsence(p) {
  selectedPupil.value = p;
  quickAbsenceDialog.value = true;
}

function openSetMaitre() {
  if (!activeClassId.value) return;
  setMaitreDialog.value = true;
}

function pickColor(c) {
  const palette = ["blue", "purple", "orange", "green", "pink"];
  return palette[Math.abs(Number(c?.id || 0)) % palette.length];
}

function goToPupil(p) {
  router.push({ name: "pupil-profile", params: { id: p.id } });
}

function extractApiError(err, fallback = "Une erreur est survenue.") {
  const status = err?.response?.status;
  const data = err?.response?.data;
  const msg =
    (typeof data === "string" && data) ||
    data?.message ||
    data?.error ||
    err?.message;

  return status ? `[${status}] ${msg || fallback}` : msg || fallback;
}

function goToAllClasses() {
  // TODO: route si tu as une page liste de classes
  // router.push({ name: "classrooms" })
  // En attendant, on ne fait rien.
}

function goToActivity() {}

function actionAddPupil() {
  // router.push({ name: "pupil-create" })
}
function actionAddClass() {
  // router.push({ name: "classroom-create" })
}
function actionCreateReport() {
  // router.push({ name: "reports" })
}
function actionSchedule() {
  // router.push({ name: "calendar" })
}

/* -----------------------------
  Helpers (labels/colors)
------------------------------ */
function classLabel(c) {
  if (!c) return "";
  return c.name || c.nom || `Classe #${c.id}`;
}

function teacherLabel(c) {
  if (!c) return "Enseignant non renseigné";
  const ln = (c.teacherLastName || "").trim();
  const fn = (c.teacherFirstName || "").trim();
  const full = `${ln ? ln.toUpperCase() : ""} ${fn}`.trim();
  return full || "Enseignant non renseigné";
}

function cycleLabel(c) {
  // Si tu as déjà cycle/level dans l'objet classe, branche ici.
  // Sinon on déduit grossièrement du nom.
  const n = (classLabel(c) || "").toUpperCase();
  if (n.includes("CP") || n.includes("CE1") || n.includes("CE2"))
    return "Cycle 2";
  if (n.includes("CM1") || n.includes("CM2")) return "Cycle 3";
  return "Cycle";
}

function initials(firstName, lastName) {
  const f = (firstName || "").trim().charAt(0).toUpperCase();
  const l = (lastName || "").trim().charAt(0).toUpperCase();
  return f + l || "—";
}

function avatarColor(gender) {
  if (String(gender).toUpperCase() === "GIRL") return "pink";
  if (String(gender).toUpperCase() === "BOY") return "blue";
  return "grey";
}

function accentVuetifyColor(c) {
  // Palette proche de la maquette
  const palette = ["primary", "deep-purple", "deep-orange", "green"];
  const idx = Math.abs(Number(c?.id || 0)) % palette.length;
  return palette[idx];
}

function accentColor(c) {
  // Couleur CSS pour la bordure gauche (dépend du thème Vuetify)
  const map = {
    primary: "rgb(var(--v-theme-primary))",
    "deep-purple": "rgb(var(--v-theme-deepPurple))",
    "deep-orange": "rgb(var(--v-theme-deepOrange))",
    green: "rgb(var(--v-theme-green))",
  };
  // fallback si tes tokens de thème ne sont pas tous définis :
  const vu = accentVuetifyColor(c);
  return map[vu] || "rgb(var(--v-theme-primary))";
}

function pupilsCount(c) {
  const direct = c?.pupilsCount ?? c?.pupilCount ?? c?.nbEleves;
  if (typeof direct === "number") return direct;

  // Check cached count from store
  const cached = pupil.pupilCountByClassroom?.[c?.id];
  if (typeof cached === "number") return cached;

  // If currently active and pupils loaded
  if (String(c?.id) === String(activeClassId.value)) {
    return Array.isArray(pupil.pupils) ? pupil.pupils.length : 0;
  }
  return 0;
}

function coursesCount(c) {
  // TODO endpoint cours/ressources
  return c?.coursesCount ?? c?.nbCours ?? 12; // 12 pour coller à la maquette (placeholder)
}

/* -----------------------------
  Activity (placeholder)
------------------------------ */
const recentActivity = ref([
  {
    icon: "mdi-check-circle",
    color: "green",
    title: "Rapport hebdomadaire généré",
    subtitle: "CE2 • École du Parc",
    when: "Il y a 2h",
  },
  {
    icon: "mdi-account-plus",
    color: "primary",
    title: "Nouvel élève inscrit",
    subtitle: "Marie Dupont • CP",
    when: "Il y a 4h",
  },
  {
    icon: "mdi-alert-circle",
    color: "deep-orange",
    title: "Absence signalée",
    subtitle: "3 élèves • CM1",
    when: "Aujourd’hui",
  },
  {
    icon: "mdi-clock-outline",
    color: "grey-darken-2",
    title: "Réunion planifiée",
    subtitle: "Conseil des enseignants",
    when: "Demain 14h",
  },
]);
</script>

<style scoped>
.class-card {
  border-left: 4px solid;
  cursor: pointer;
  transition:
    transform 120ms ease,
    box-shadow 120ms ease;
}
.class-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.06);
}

.avatar-initials {
  font-weight: 800;
  font-size: 12px;
}

.quick-action {
  cursor: pointer;
  transition:
    transform 120ms ease,
    box-shadow 120ms ease;
}
.quick-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 22px rgba(0, 0, 0, 0.06);
}

/* bouton "voir toutes les classes" en style pointillé */
.dashed-btn {
  border: 1px dashed rgba(0, 0, 0, 0.15);
  border-radius: 14px;
}

.dashboard {
  padding: 32px 24px;
}

.dashboard__inner {
  max-width: 1280px;
  margin: 0 auto;
}

.button {
  background: linear-gradient(90deg, #2563eb, #3b82f6) !important;
  color: white !important;
}
</style>
