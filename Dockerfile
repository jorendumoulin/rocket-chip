# FROM ubuntu:24.04
#
# # Install apt packages
# RUN apt-get update && apt-get -y install curl default-jdk git make
#
# # Install mill
# RUN curl -L https://github.com/com-lihaoyi/mill/releases/download/0.10.15/0.10.15 > mill && \
#     chmod +x mill && \
#     mv mill /usr/bin/mill && \
#     mill --version

# Clone rocket-chip


FROM nixos/nix

RUN git clone https://github.com/chipsalliance/chipsalliance/rocket-chip --recursive

RUN cd rocket-chip && \
    nix --experimental-features 'nix-command flakes' \
    develop -c mill -i "emulator[freechips.rocketchip.system.TestHarness,freechips.rocketchip.system.DefaultSmallConfig].elf"

# for snax:
RUN nix --experimental-features 'nix-command flakes' develop -c mill -i "emulator[freechips.rocketchip.snax.SnaxSystem,freechips.rocketchip.snax.SnaxConfig].rtls"

# preprocess into a single file

RUN nix --experimental-features 'nix-command flakes' develop -c verilator -E ./out/emulator/freechips.rocketchip.snax.SnaxCore/freechips.rocketchip.snax.SnaxConfig/mfccompiler/compile.dest/*v > test.sv
