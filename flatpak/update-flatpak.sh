#! /bin/sh
set -e -u

usage() {
    cat<<EOF
Usage: $(basename $0) [OPTION ...] VERSION
  -h    Display this help and exit
  -v    Be more verbose
EOF
}

# Global Variables
verbose=false
target=""
needs_programs="flatpak-builder"
flatpak_manifest="io.github.syt0r.kanji-dojo.yml"
application_metainfo="io.github.syt0r.kanji-dojo.metainfo.xml"
application_changelog="core/src/commonMain/kotlin/ua/syt0r/kanji/presentation/dialog/VersionChangeDialog.kt"
new_release_jar=""

# Helper Functions
## External dependency check
check_dependencies() {
    local error=false
    local program
    for program in ${needs_programs}; do
        command -v ${program} >/dev/null || {
            echo "Please install ${program}"
            error=true
        }
    done
    [ ${error} = true ] && exit -1 ||:
}

## Trap function
cleanup() {
    [ $? -ne 0 ] && echo "Error: an unexpected error occured, cleaning up and exiting" ||:
    rm -rf Kanji-Dojo ||:
    rm -rf ${new_release_jar} ||:
}
trap cleanup EXIT

## Parameter Parser
get_param() {
    local opt
    local OPTIND
    local OPTARG
    while getopts "hv" opt; do
        case "$opt" in
        h) usage; exit 0 ;;
        v) verbose=true ;;
        esac
    done
    # Done so that $@ contains the rest
    shift $(( $OPTIND - 1 ))
    target="$@"
}

##############
#    MAIN    #
##############

# Preconditions
check_dependencies

# Process Parameters
get_param "$@"
## Target is required
[ -z "${target}" ] && echo "Error: missing version number" && usage && exit 0
new_version_number=${target}

# Get new commit hash and substitute it in the manifest
git clone --depth 1 --branch v${new_version_number} git@github.com:syt0r/Kanji-Dojo.git
new_version_commit_hash=$(git -C Kanji-Dojo log -n 1 | head -1 | cut -f 2 -d " ")
sed -i -E "s/commit: [0-9a-f]+/commit: ${new_version_commit_hash}/g" ${flatpak_manifest}

# Change manifest to use new version number
sed -i -E "s/(kanji-dojo-linux-x64)-[0-9]+\.[0-9]+\.[0-9]+/\1-${new_version_number}/g" ${flatpak_manifest}
sed -i -E "s/tag: v[0-9]+\.[0-9+]\.[0-9]+/tag: v${new_version_number}/g" ${flatpak_manifest}
sed -i -E "s|https://github.com/syt0r/Kanji-Dojo/releases/download/v[0-9]+\.[0-9+]\.[0-9]+/|https://github.com/syt0r/Kanji-Dojo/releases/download/v${new_version_number}/|g" ${flatpak_manifest}

# Grab the release jar and generate the sha256sum for the manifest
new_release_jar_uri=$(grep "https://github.com/syt0r/Kanji-Dojo/releases/download/v" ${flatpak_manifest} | cut -f 2- -d ":")
new_release_jar=kanji-dojo-linux-x64-v${new_version_number}.jar
curl -L ${new_release_jar_uri} -o ${new_release_jar}
new_release_jar_sha256=$(sha256sum ${new_release_jar} | cut -f 1 -d " ")
sed -i -E "s/sha256: [0-9a-f]+/sha256: ${new_release_jar_sha256}/g" ${flatpak_manifest}

# Add release notes into metainfo
append_from_line_number=$(grep --line-number "<releases>" ${application_metainfo} | cut -f 1 -d ":")

version_info_header=$(grep --max-count=2 --line-number -E "version\(\"[0-9]+\.[0-9]+\.[0-9]+\", LocalDate" Kanji-Dojo/${application_changelog})
version_release_date=$(date -I -d "$(echo ${version_info_header} | head -1 | cut -f 2- -d "," | sed -E 's/ LocalDate\(([0-9]+), ([0-9]+), ([0-9]+)\).*/\1-\2-\3/g')")
version_info_start_line=$(echo "${version_info_header}" | head -1 | cut -f 1 -d ":")
  version_info_end_line=$(echo "${version_info_header}" | tail -1 | cut -f 1 -d ":")

version_info=$(sed -n "$((version_info_start_line + 3)),$((version_info_end_line - 4))p" Kanji-Dojo/${application_changelog}\
    | sed -E 's| +- (.+)|          <li>\1</li>|'\
    | tr -d '\n')

# Inserted backwards to avoid having to count lines
sed -i "${append_from_line_number}a\    </release>" ${application_metainfo}
sed -i "${append_from_line_number}a\      </description>" ${application_metainfo}
sed -i "${append_from_line_number}a\ ${version_info}" ${application_metainfo}
sed -i "s|(</li> |</li>\\\n <li>|" ${application_metainfo}
sed -i "${append_from_line_number}a\      <description>" ${application_metainfo}
sed -i "${append_from_line_number}a\    <release version=\"${new_version_number}\" date=\"${version_release_date}\">" ${application_metainfo}

cat <<EOL
      </description>
    </release>
EOL

# Run
exit 0
