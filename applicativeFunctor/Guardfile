# A sample Guardfile
# More info at https://github.com/guard/guard#readme

guard :shell do
  watch(%r{^src/*/.+\.scala$}) { `gradle test` }
end

